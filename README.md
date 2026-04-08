# Country Service intro and funtionality

A reactive Spring Boot microservice that exposes a REST API for browsing country information, backed by the [REST Countries API](https://restcountries.com). Includes a companion single-page frontend application.

## Prerequisites

Java (JDK)  17 or later 
Maven  3.8 or later 


## Running locally

mvn spring-boot:run

The service starts on **http://localhost:8080**.

## Open the frontend

Simply open `frontend/index.html` in your browser:




All responses use `Content-Type: application/json`.

### `GET /countries/`

Returns a sorted list of all countries.

**Response 200**

json
{
  "countries": [
    { "name": "Afghanistan", "country_code": "AF" },
    { "name": "Albania",     "country_code": "AL" },
    ...
  ]
}


### GET /countries/{name}

Returns detailed information for a single country. The name must match exactly (e.g. `Finland`, `United Kingdom`).

**Response 200**

json
{
  "name":          "Finland",
  "country_code":  "FI",
  "capital":       "Helsinki",
  "population":    5491817,
  "flag_file_url": "https://flagcdn.com/fi.svg"
}


**Response 404** (country not found)

json
{
  "type":     "https://example.com/errors/country-not-found",
  "title":    "Country Not Found",
  "status":   404,
  "detail":   "Country not found: Neverland"
}

