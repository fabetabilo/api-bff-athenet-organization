package com.athenet.bff_organization.service.director;

import java.util.List;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.client.events.EventsClient;
import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.service.common.InstitutionsSnapshot;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;
import com.athenet.bff_organization.web.director.dto.response.EventTypeOptionResponse;

import lombok.RequiredArgsConstructor;

/**
 * CRUD de eventos del director (ms-events) y catalogo de tipos de evento.
 * Las referencias de cada evento se resuelven con el snapshot de ms-institutions.
 */
@Service
@RequiredArgsConstructor
public class DirectorEventService {

	private final EventsClient eventsClient;
	private final CatalogService catalogService;
	private final DirectorEventMapper directorEventMapper;

	/** Todos los eventos, en cualquier estado */
	public List<DirectorEventResponse> getEvents() {
		return directorEventMapper.toDirectorEventResponses(eventsClient.getAllEvents(),
				catalogService.getInstitutionsSnapshot());
	}

	public DirectorEventResponse getEvent(String internalId) {
		return directorEventMapper.toDirectorEventResponse(eventsClient.getEvent(internalId),
				catalogService.getInstitutionsSnapshot());
	}

	// En las escrituras el snapshot se carga antes: si ms-institutions no responde, el evento no se crea ni se edita
	// TODO: validar institucion → sede → equipo con el snapshot antes de crear/editar
	public DirectorEventResponse createEvent(DirectorEventRequest request) {
		InstitutionsSnapshot refs = catalogService.getInstitutionsSnapshot();
		return directorEventMapper.toDirectorEventResponse(
				eventsClient.createEvent(directorEventMapper.toEventRequest(request)), refs);
	}

	public DirectorEventResponse updateEvent(String internalId, DirectorEventRequest request) {
		InstitutionsSnapshot refs = catalogService.getInstitutionsSnapshot();
		return directorEventMapper.toDirectorEventResponse(
				eventsClient.updateEvent(internalId, directorEventMapper.toEventRequest(request)), refs);
	}

	public void deleteEvent(String internalId) {
		eventsClient.deleteEvent(internalId);
	}

	public List<EventTypeOptionResponse> getEventTypes() {
		return directorEventMapper.toEventTypeOptions(eventsClient.getEventTypes());
	}

}
