package com.example.countryservice.exception;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(String name) {
        super("Country not found: " + name);
    }
}
