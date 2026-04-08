package com.example.countryservice.service;

import com.example.countryservice.exception.CountryNotFoundException;
import com.example.countryservice.model.RestCountryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private RestCountriesClient client;

    @InjectMocks
    private CountryService countryService;

    private RestCountryDto finland;
    private RestCountryDto germany;

    @BeforeEach
    void setUp() {
        finland = buildDto("Finland", "FI", "Helsinki", 5500000, "https://flagcdn.com/fi.svg");
        germany = buildDto("Germany", "DE", "Berlin", 83000000, "https://flagcdn.com/de.svg");
    }

    @Test
    @DisplayName("getAllCountries returns sorted list of country summaries")
    void getAllCountries_returnsSortedList() {
        when(client.fetchAllCountries()).thenReturn(Flux.just(germany, finland));

        StepVerifier.create(countryService.getAllCountries())
                .assertNext(response -> {
                    assertThat(response.getCountries()).hasSize(2);
                    // Alphabetical: Finland before Germany
                    assertThat(response.getCountries().get(0).getName()).isEqualTo("Finland");
                    assertThat(response.getCountries().get(0).getCountryCode()).isEqualTo("FI");
                    assertThat(response.getCountries().get(1).getName()).isEqualTo("Germany");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("getCountryByName returns full detail for a known country")
    void getCountryByName_returnsDetail() {
        when(client.fetchCountryByName("Finland")).thenReturn(Mono.just(finland));

        StepVerifier.create(countryService.getCountryByName("Finland"))
                .assertNext(detail -> {
                    assertThat(detail.getName()).isEqualTo("Finland");
                    assertThat(detail.getCountryCode()).isEqualTo("FI");
                    assertThat(detail.getCapital()).isEqualTo("Helsinki");
                    assertThat(detail.getPopulation()).isEqualTo(5500000L);
                    assertThat(detail.getFlagFileUrl()).isEqualTo("https://flagcdn.com/fi.svg");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("getCountryByName propagates CountryNotFoundException")
    void getCountryByName_notFound_propagatesException() {
        when(client.fetchCountryByName("Neverland"))
                .thenReturn(Mono.error(new CountryNotFoundException("Neverland")));

        StepVerifier.create(countryService.getCountryByName("Neverland"))
                .expectError(CountryNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("getAllCountries returns empty list when upstream is empty")
    void getAllCountries_emptyUpstream_returnsEmptyList() {
        when(client.fetchAllCountries()).thenReturn(Flux.empty());

        StepVerifier.create(countryService.getAllCountries())
                .assertNext(response -> assertThat(response.getCountries()).isEmpty())
                .verifyComplete();
    }

    // --- helpers ---

    private RestCountryDto buildDto(String name, String code, String capital,
                                    long population, String flagUrl) {
        RestCountryDto dto = new RestCountryDto();

        RestCountryDto.NameDto nameDto = new RestCountryDto.NameDto();
        nameDto.setCommon(name);
        nameDto.setOfficial(name);
        dto.setName(nameDto);

        dto.setCca2(code);
        dto.setCapital(List.of(capital));
        dto.setPopulation(population);

        RestCountryDto.FlagsDto flags = new RestCountryDto.FlagsDto();
        flags.setSvg(flagUrl);
        dto.setFlags(flags);

        return dto;
    }
}
