package com.example.countryservice.service;

import com.example.countryservice.exception.CountryNotFoundException;
import com.example.countryservice.exception.UpstreamServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class RestCountriesClientTest {

    private MockWebServer mockServer;
    private RestCountriesClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockServer.url("/").toString())
                .build();
        client = new RestCountriesClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("fetchAllCountries deserializes the upstream JSON correctly")
    void fetchAllCountries_parsesResponse() {
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        [
                          {
                            "name": {"common": "Finland", "official": "Republic of Finland"},
                            "cca2": "FI",
                            "capital": ["Helsinki"],
                            "population": 5491817,
                            "flags": {"png": "https://flagcdn.com/w320/fi.png", "svg": "https://flagcdn.com/fi.svg"}
                          }
                        ]
                        """));

        StepVerifier.create(client.fetchAllCountries())
                .assertNext(dto -> {
                    assert "Finland".equals(dto.getCommonName());
                    assert "FI".equals(dto.getCca2());
                    assert "Helsinki".equals(dto.getCapitalCity());
                    assert dto.getPopulation() == 5491817L;
                    assert "https://flagcdn.com/fi.svg".equals(dto.getFlagUrl());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("fetchCountryByName emits CountryNotFoundException on 404")
    void fetchCountryByName_404_throwsCountryNotFoundException() {
        mockServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(client.fetchCountryByName("Neverland"))
                .expectError(CountryNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("fetchAllCountries emits UpstreamServiceException on 500")
    void fetchAllCountries_500_throwsUpstreamServiceException() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(client.fetchAllCountries())
                .expectError(UpstreamServiceException.class)
                .verify();
    }
}
