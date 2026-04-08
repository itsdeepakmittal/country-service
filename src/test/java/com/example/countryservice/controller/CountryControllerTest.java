package com.example.countryservice.controller;

import com.example.countryservice.exception.CountryNotFoundException;
import com.example.countryservice.model.CountryDetail;
import com.example.countryservice.model.CountryListResponse;
import com.example.countryservice.model.CountrySummary;
import com.example.countryservice.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CountryService countryService;

    @Test
    @DisplayName("GET /countries/ returns 200 with country list")
    void getCountries_returns200() {
        CountryListResponse response = new CountryListResponse(List.of(
                new CountrySummary("Finland", "FI"),
                new CountrySummary("Germany", "DE")
        ));
        when(countryService.getAllCountries()).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/countries/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.countries").isArray()
                .jsonPath("$.countries.length()").isEqualTo(2)
                .jsonPath("$.countries[0].name").isEqualTo("Finland")
                .jsonPath("$.countries[0].country_code").isEqualTo("FI");
    }

    @Test
    @DisplayName("GET /countries/{name} returns 200 with country detail")
    void getCountry_returns200() {
        CountryDetail detail = CountryDetail.builder()
                .name("Finland")
                .countryCode("FI")
                .capital("Helsinki")
                .population(5491817L)
                .flagFileUrl("https://flagcdn.com/fi.svg")
                .build();

        when(countryService.getCountryByName("Finland")).thenReturn(Mono.just(detail));

        webTestClient.get()
                .uri("/countries/Finland")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Finland")
                .jsonPath("$.country_code").isEqualTo("FI")
                .jsonPath("$.capital").isEqualTo("Helsinki")
                .jsonPath("$.population").isEqualTo(5491817)
                .jsonPath("$.flag_file_url").isEqualTo("https://flagcdn.com/fi.svg");
    }

    @Test
    @DisplayName("GET /countries/{name} returns 404 when country not found")
    void getCountry_returns404_whenNotFound() {
        when(countryService.getCountryByName("Neverland"))
                .thenReturn(Mono.error(new CountryNotFoundException("Neverland")));

        webTestClient.get()
                .uri("/countries/Neverland")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
