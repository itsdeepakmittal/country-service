package com.example.countryservice.service;

import com.example.countryservice.exception.CountryNotFoundException;
import com.example.countryservice.exception.UpstreamServiceException;
import com.example.countryservice.model.RestCountryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive client wrapping the restcountries.com v3.1 API.
 * All methods return Reactor publishers (Flux/Mono) so callers
 * stay fully non-blocking.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestCountriesClient {

    private final WebClient restCountriesWebClient;

    /**
     * Fetches every country (only the fields we actually need via the `fields` param).
     */
    public Flux<RestCountryDto> fetchAllCountries() {
        return restCountriesWebClient.get()
                .uri("/all?fields=name,cca2,capital,population,flags")
                .retrieve()
                .bodyToFlux(RestCountryDto.class)
                .doOnError(e -> log.error("Error fetching all countries", e))
                .onErrorMap(WebClientResponseException.class, this::mapWebClientError)
                .onErrorMap(e -> !(e instanceof UpstreamServiceException),
                        e -> new UpstreamServiceException("Failed to reach upstream service", e));
    }

    /**
     * Fetches a single country by common name (case-insensitive).
     * Emits {@link CountryNotFoundException} if not found.
     */
    public Mono<RestCountryDto> fetchCountryByName(String name) {
        return restCountriesWebClient.get()
                .uri("/name/{name}?fullText=true&fields=name,cca2,capital,population,flags", name)
                .retrieve()
                .bodyToFlux(RestCountryDto.class)
                .next() // The API returns an array; we want the first (exact) match
                .switchIfEmpty(Mono.error(new CountryNotFoundException(name)))
                .doOnError(e -> log.error("Error fetching country: {}", name, e))
                .onErrorMap(WebClientResponseException.class, this::mapWebClientError)
                .onErrorMap(e -> !(e instanceof CountryNotFoundException)
                                && !(e instanceof UpstreamServiceException),
                        e -> new UpstreamServiceException("Failed to reach upstream service", e));
    }

    private RuntimeException mapWebClientError(WebClientResponseException ex) {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            // Extract the country name from the request URI for a helpful message
            String uri = ex.getRequest() != null ? ex.getRequest().getURI().toString() : "";
            String country = uri.contains("/name/") ? uri.replaceAll(".*/name/([^?]+).*", "$1") : "unknown";
            return new CountryNotFoundException(country);
        }
        return new UpstreamServiceException(
                "Upstream returned HTTP " + ex.getStatusCode(), ex);
    }
}
