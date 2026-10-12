package com.athenet.bff_organization.client.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.athenet.bff_organization.client.events.dto.request.EventRequest;
import com.athenet.bff_organization.client.events.dto.response.EventResponse;
import com.athenet.bff_organization.client.events.dto.response.EventTypeResponse;

@Component
public class EventsClient {

	private final RestClient restClient;

	public EventsClient(@Qualifier("eventsRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	/** Todos los eventos, en cualquier estado (DRAFT, PUBLISHED, CANCELLED) */
	public List<EventResponse> getAllEvents() {
		return restClient.get()
				.uri("/api/admin/events")
				.retrieve()
				.body(new ParameterizedTypeReference<List<EventResponse>>() {});
	}

	/** Detalle de un evento en cualquier estado. Requiere rol DIRECTOR */
	public EventResponse getEvent(String internalId) {
		return restClient.get()
				.uri("/api/admin/events/{internalId}", internalId)
				.retrieve()
				.body(EventResponse.class);
	}

	/** Crea un evento (201). Requiere rol DIRECTOR */
	public EventResponse createEvent(EventRequest request) {
		return restClient.post()
				.uri("/api/admin/events")
				.body(request)
				.retrieve()
				.body(EventResponse.class);
	}

	/** Actualiza un evento; el internalId no cambia. Requiere rol DIRECTOR */
	public EventResponse updateEvent(String internalId, EventRequest request) {
		return restClient.put()
				.uri("/api/admin/events/{internalId}", internalId)
				.body(request)
				.retrieve()
				.body(EventResponse.class);
	}

	/** Elimina un evento (204). Requiere rol DIRECTOR */
	public void deleteEvent(String internalId) {
		restClient.delete()
				.uri("/api/admin/events/{internalId}", internalId)
				.retrieve()
				.toBodilessEntity();
	}

	/** Catalogo de tipos de evento. Requiere rol DIRECTOR */
	public List<EventTypeResponse> getEventTypes() {
		return restClient.get()
				.uri("/api/admin/event-types")
				.retrieve()
				.body(new ParameterizedTypeReference<List<EventTypeResponse>>() {});
	}

}
