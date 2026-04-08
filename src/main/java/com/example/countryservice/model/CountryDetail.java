package com.example.countryservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDetail {

    @JsonProperty("name")
    private String name;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("capital")
    private String capital;

    @JsonProperty("population")
    private long population;

    @JsonProperty("flag_file_url")
    private String flagFileUrl;
}
