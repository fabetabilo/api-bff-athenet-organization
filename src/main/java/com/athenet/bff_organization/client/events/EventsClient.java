package com.athenet.bff_organization.client.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.athenet.bff_organization.client.events.dto.response.EventResponse;

@Component
public class EventsClient {

	private final RestClient restClient;

	public EventsClient(@Qualifier("eventsRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public List<EventResponse> getPublicEvents() {
		return restClient.get()
				.uri("/api/public/events")
				.retrieve()
				.body(new ParameterizedTypeReference<List<EventResponse>>() {});
	}

}
