package com.athenet.bff_organization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean
	public RestClient eventsRestClient(RestClient.Builder builder,
			@Value("${athenet.ms.events.url}") String eventsUrl) {
		return builder.baseUrl(eventsUrl).requestInterceptor(bearerTokenRelay()).build();
	}

	/** Reenvia al microservicio el mismo token con el que el usuario llamo al BFF. Sin token, no agrega nada */
	private static ClientHttpRequestInterceptor bearerTokenRelay() {
		return (request, body, execution) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
				request.getHeaders().setBearerAuth(jwtAuthentication.getToken().getTokenValue());
			}
			return execution.execute(request, body);
		};
	}

}
