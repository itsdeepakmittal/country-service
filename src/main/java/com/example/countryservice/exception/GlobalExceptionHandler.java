package com.example.countryservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CountryNotFoundException.class)
    public ProblemDetail handleNotFound(CountryNotFoundException ex) {
        log.warn("Country not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://example.com/errors/country-not-found"));
        problem.setTitle("Country Not Found");
        return problem;
    }

    @ExceptionHandler(UpstreamServiceException.class)
    public ProblemDetail handleUpstreamError(UpstreamServiceException ex) {
        log.error("Upstream service error: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "Failed to fetch data from upstream country service"
        );
        problem.setType(URI.create("https://example.com/errors/upstream-error"));
        problem.setTitle("Upstream Service Unavailable");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problem.setType(URI.create("https://example.com/errors/internal-error"));
        problem.setTitle("Internal Server Error");
        return problem;
    }
}
