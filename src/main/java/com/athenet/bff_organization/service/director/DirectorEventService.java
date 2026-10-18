package com.athenet.bff_organization.service.director;

import java.util.List;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.client.events.EventsClient;
import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.service.common.InstitutionsSnapshot;
import com.athenet.bff_organization.service.exception.ServiceException;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;
import com.athenet.bff_organization.web.director.dto.response.EventTypeOptionResponse;

/**
 * CRUD de eventos del director (ms-events) y catalogo de tipos de evento.
 * Las referencias de cada evento se resuelven con el snapshot de ms-institutions: en las lecturas, si no
 * responde, van con name en null; en las escrituras, el evento no se crea ni se edita.
 */
@Service
public class DirectorEventService {

	private static final String MATCH_TYPE = "MATCH";

	private final EventsClient eventsClient;
	private final CatalogService catalogService;
	private final DirectorEventMapper directorEventMapper;
	private final EventReferenceValidator referenceValidator;

	public DirectorEventService(EventsClient eventsClient, CatalogService catalogService,
			DirectorEventMapper directorEventMapper, EventReferenceValidator referenceValidator) {
		this.eventsClient = eventsClient;
		this.catalogService = catalogService;
		this.directorEventMapper = directorEventMapper;
		this.referenceValidator = referenceValidator;
	}

	/** Todos los eventos, en cualquier estado */
	public List<DirectorEventResponse> getEvents() {
		return directorEventMapper.toDirectorEventResponses(eventsClient.getAllEvents(),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	public DirectorEventResponse getEvent(String internalId) {
		return directorEventMapper.toDirectorEventResponse(eventsClient.getEvent(internalId),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	// En las escrituras el snapshot se carga antes y sin respaldo: con ms-institutions sin responder no se escribe,
	// para no guardar un evento editado sobre datos incompletos
	public DirectorEventResponse createEvent(DirectorEventRequest request) {
		rejectMatch(request);
		InstitutionsSnapshot refs = catalogService.getInstitutionsSnapshot();
		referenceValidator.validate(request, refs);
		return directorEventMapper.toDirectorEventResponse(
				eventsClient.createEvent(directorEventMapper.toEventRequest(request)), refs);
	}

	public DirectorEventResponse updateEvent(String internalId, DirectorEventRequest request) {
		rejectCompetitionCopy(eventsClient.getEvent(internalId));
		rejectMatch(request);
		InstitutionsSnapshot refs = catalogService.getInstitutionsSnapshot();
		referenceValidator.validate(request, refs);
		return directorEventMapper.toDirectorEventResponse(
				eventsClient.updateEvent(internalId, directorEventMapper.toEventRequest(request)), refs);
	}

	public void deleteEvent(String internalId) {
		rejectCompetitionCopy(eventsClient.getEvent(internalId));
		eventsClient.deleteEvent(internalId);
	}

	private static void rejectCompetitionCopy(
			com.athenet.bff_organization.client.events.dto.response.EventResponse existing) {
		if (existing.competitionId() != null || existing.competitionMatchId() != null) {
			throw new ServiceException(ServiceException.Type.CONFLICT, "COMPETITION_EVENT_READ_ONLY",
					"Los encuentros se gestionan desde su competencia.");
		}
	}

	/** Sin los tipos con enfrentamiento: los partidos se crean desde una competencia */
	public List<EventTypeOptionResponse> getEventTypes() {
		return directorEventMapper.toEventTypeOptions(eventsClient.getEventTypes().stream()
				.filter(type -> !type.requiresMatchup())
				.toList());
	}

	// Los partidos los crea ms-competitions y ms-events guarda su copia: el director no los crea a mano
	private static void rejectMatch(DirectorEventRequest request) {
		if (MATCH_TYPE.equals(request.type())) {
			String message = "Los partidos se crean desde una competencia.";
			throw new ServiceException(ServiceException.Type.VALIDATION_FAILED, "VALIDATION_FAILED", message,
					List.of(new ServiceException.FieldDetail("type", "MATCH_NOT_ALLOWED", message)));
		}
	}

}
