package com.athenet.bff_organization.service.director;

import java.util.List;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.client.events.dto.request.EventRequest;
import com.athenet.bff_organization.client.events.dto.response.EventResponse;
import com.athenet.bff_organization.client.events.dto.response.EventTypeResponse;
import com.athenet.bff_organization.service.mock.InstitutionsMock;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;
import com.athenet.bff_organization.web.director.dto.response.EventTypeOptionResponse;

import lombok.RequiredArgsConstructor;

/**
 * Lectura: EventResponse (plano, con IDs) → DirectorEventResponse (referencias resueltas, sin id de BD).
 * Escritura: DirectorEventRequest → EventRequest.
 */
@Component
@RequiredArgsConstructor
public class DirectorEventMapper {

	private final InstitutionsMock institutionsMock;

	public List<DirectorEventResponse> toDirectorEventResponses(List<EventResponse> events) {
		return events.stream().map(this::toDirectorEventResponse).toList();
	}

	public DirectorEventResponse toDirectorEventResponse(EventResponse e) {
		return new DirectorEventResponse(
				e.internalId(),
				e.title(),
				e.description(),
				e.descriptionOpt(),
				e.type(),
				e.status(),
				e.isOfficial(),
				e.eventDate(),
				e.startTime(),
				e.endDate(),
				e.endTime(),
				e.location(),
				e.address(),
				e.coverImage(),
				e.photos(),
				institutionsMock.sport(e.sportId()),
				institutionsMock.organizer(e.organizerInstitutionId(), e.organizerCampusId(), e.organizerTeamId()),
				institutionsMock.matchTeam(e.teamOneId()),
				institutionsMock.matchTeam(e.teamTwoId()),
				e.delegateOid() != null ? new DirectorEventResponse.Delegate(e.delegateOid(), e.delegateDisplayName()) : null);
	}

	/** No reenvia teamXInstitutionId ni teamXCampusId (solo sirven para validar) */
	public EventRequest toEventRequest(DirectorEventRequest r) {
		return new EventRequest(
				r.internalId(),
				r.title(),
				r.description(),
				r.descriptionOpt(),
				r.type(),
				r.status(),
				r.isOfficial(),
				r.eventDate(),
				r.startTime(),
				r.endDate(),
				r.endTime(),
				r.location(),
				r.address(),
				r.coverImage(),
				r.photos(),
				r.sportId(),
				r.organizerInstitutionId(),
				r.organizerCampusId(),
				r.organizerTeamId(),
				r.teamOneId(),
				r.teamTwoId(),
				r.delegateOid(),
				r.delegateDisplayName());
	}

	public List<EventTypeOptionResponse> toEventTypeOptions(List<EventTypeResponse> types) {
		return types.stream()
				.map(t -> new EventTypeOptionResponse(t.code(), t.label(), t.requiresMatchup()))
				.toList();
	}

}
