package com.athenet.bff_organization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean
	public RestClient eventsRestClient(RestClient.Builder builder,
			@Value("${athenet.ms.events.url}") String eventsUrl) {
		return builder.baseUrl(eventsUrl).build();
	}

}
