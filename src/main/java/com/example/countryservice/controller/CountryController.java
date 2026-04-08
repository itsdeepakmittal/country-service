package com.example.countryservice.controller;

import com.example.countryservice.model.CountryDetail;
import com.example.countryservice.model.CountryListResponse;
import com.example.countryservice.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for country endpoints.
 *
 * All handler methods return Reactor Mono/Flux, making the entire
 * request-handling pipeline non-blocking (Spring WebFlux).
 */
@Slf4j
@RestController
@RequestMapping(value = "/countries", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    /**
     * GET /countries/
     * Returns a summary list of all countries sorted alphabetically.
     */
    @GetMapping("/")
    public Mono<CountryListResponse> getAllCountries() {
        log.info("GET /countries/");
        return countryService.getAllCountries();
    }

    /**
     * GET /countries/{name}
     * Returns detailed information about a single country.
     * Responds with 404 if the country is not found.
     */
    @GetMapping("/{name}")
    public Mono<CountryDetail> getCountry(@PathVariable String name) {
        log.info("GET /countries/{}", name);
        return countryService.getCountryByName(name);
    }
}
