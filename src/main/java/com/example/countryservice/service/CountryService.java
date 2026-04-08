package com.example.countryservice.service;

import com.example.countryservice.model.CountryDetail;
import com.example.countryservice.model.CountryListResponse;
import com.example.countryservice.model.CountrySummary;
import com.example.countryservice.model.RestCountryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * Service layer containing the business logic for the country microservice.
 *
 * Uses Project Reactor operators to transform upstream data reactively:
 * - Flux.collectSortedList  → gather all countries into a sorted list without blocking
 * - map / flatMap            → transform types inside the reactive pipeline
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final RestCountriesClient client;

    /**
     * Returns a list of all countries (name + country_code), sorted alphabetically.
     */
    public Mono<CountryListResponse> getAllCountries() {
        return client.fetchAllCountries()
                .map(this::toSummary)
                .collectSortedList(Comparator.comparing(CountrySummary::getName))
                .map(CountryListResponse::new)
                .doOnSuccess(r -> log.info("Returning {} countries", r.getCountries().size()));
    }

    /**
     * Returns detailed information for a single country by name.
     */
    public Mono<CountryDetail> getCountryByName(String name) {
        return client.fetchCountryByName(name)
                .map(this::toDetail)
                .doOnSuccess(c -> log.info("Returning detail for {}", c.getName()));
    }

    // --- Mapping helpers ---

    private CountrySummary toSummary(RestCountryDto dto) {
        return CountrySummary.builder()
                .name(dto.getCommonName())
                .countryCode(dto.getCca2())
                .build();
    }

    private CountryDetail toDetail(RestCountryDto dto) {
        return CountryDetail.builder()
                .name(dto.getCommonName())
                .countryCode(dto.getCca2())
                .capital(dto.getCapitalCity())
                .population(dto.getPopulation())
                .flagFileUrl(dto.getFlagUrl())
                .build();
    }
}
