package com.example.countryservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Configures a WebClient pointed at the REST Countries API.
 * Uses Project Reactor's Netty HTTP client with explicit timeouts.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${restcountries.base-url}")
    private String baseUrl;

    @Value("${webclient.connect-timeout}")
    private int connectTimeout;

    @Value("${webclient.read-timeout}")
    private int readTimeout;

    @Bean
    public WebClient restCountriesWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.debug("Outbound request: {} {}", req.method(), req.url());
            return Mono.just(req);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            log.debug("Upstream response status: {}", res.statusCode());
            return Mono.just(res);
        });
    }
}
