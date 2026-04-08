package com.example.countryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO matching the restcountries.com v3.1 API response structure.
 * Fields not needed are ignored to keep the mapping lean.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestCountryDto {

    @JsonProperty("name")
    private NameDto name;

    @JsonProperty("cca2")
    private String cca2; // ISO 3166-1 alpha-2 country code (e.g. "FI")

    @JsonProperty("capital")
    private List<String> capital;

    @JsonProperty("population")
    private long population;

    @JsonProperty("flags")
    private FlagsDto flags;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NameDto {
        @JsonProperty("common")
        private String common; // e.g. "Finland"

        @JsonProperty("official")
        private String official; // e.g. "Republic of Finland"
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlagsDto {
        @JsonProperty("png")
        private String png;

        @JsonProperty("svg")
        private String svg;
    }

    // Convenience helpers

    public String getCommonName() {
        return name != null ? name.getCommon() : null;
    }

    public String getCapitalCity() {
        return (capital != null && !capital.isEmpty()) ? capital.get(0) : null;
    }

    public String getFlagUrl() {
        if (flags == null) return null;
        // Prefer SVG, fall back to PNG
        return flags.getSvg() != null ? flags.getSvg() : flags.getPng();
    }
}
