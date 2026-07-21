package org.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] eloquence) {
        SpringApplication.run(ApiGatewayApplication.class, eloquence);
    }

    // 🛡️ High Precedence Filter: intercepts preflight requests at the absolute entry gate
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter corsWebFilter() {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                var response = exchange.getResponse();
                var headers = response.getHeaders();

                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8085");
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty(); // Return 200 OK immediately
                }
            }
            return chain.filter(exchange);
        };
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        // 🔓 Let browser preflight pings bypass security checks safely
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔓 Silences the browser tab icon 403 error
                        .pathMatchers("/favicon.ico", "/favicon.svg").permitAll()

                        // 🔓 Let your connection test loop bypass token verification
                        .pathMatchers("/rooms/test-connection").permitAll()

                        // 🔐 Require authentication for search so the gateway validates the Keycloak JWT
                        .pathMatchers(HttpMethod.GET, "/hotels/search/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/hotels/search").authenticated()

                        // All core logic business endpoints still strictly require authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}