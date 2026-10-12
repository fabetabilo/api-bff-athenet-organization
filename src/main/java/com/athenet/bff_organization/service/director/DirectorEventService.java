package com.athenet.bff_organization.service.director;

import java.util.List;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.client.events.EventsClient;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;
import com.athenet.bff_organization.web.director.dto.response.EventTypeOptionResponse;

import lombok.RequiredArgsConstructor;

/** CRUD de eventos del director y catalogo de tipos de evento (ms-events) */
@Service
@RequiredArgsConstructor
public class DirectorEventService {

	private final EventsClient eventsClient;
	private final DirectorEventMapper directorEventMapper;

	/** Todos los eventos, en cualquier estado */
	public List<DirectorEventResponse> getEvents() {
		return directorEventMapper.toDirectorEventResponses(eventsClient.getAllEvents());
	}

	public DirectorEventResponse getEvent(String internalId) {
		return directorEventMapper.toDirectorEventResponse(eventsClient.getEvent(internalId));
	}

	// TODO: validar institucion → sede → equipo contra ms-institutions antes de crear/editar
	public DirectorEventResponse createEvent(DirectorEventRequest request) {
		return directorEventMapper.toDirectorEventResponse(
				eventsClient.createEvent(directorEventMapper.toEventRequest(request)));
	}

	public DirectorEventResponse updateEvent(String internalId, DirectorEventRequest request) {
		return directorEventMapper.toDirectorEventResponse(
				eventsClient.updateEvent(internalId, directorEventMapper.toEventRequest(request)));
	}

	public void deleteEvent(String internalId) {
		eventsClient.deleteEvent(internalId);
	}

	public List<EventTypeOptionResponse> getEventTypes() {
		return directorEventMapper.toEventTypeOptions(eventsClient.getEventTypes());
	}

}
