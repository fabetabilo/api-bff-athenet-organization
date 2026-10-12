package com.athenet.bff_organization.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.client.events.dto.response.EventResponse;
import com.athenet.bff_organization.web.dto.response.PublicEventResponse;

/** Traduce la respuesta del microservicio (EventResponse) a la respuesta para la web (PublicEventResponse) */
@Component
public class PublicEventMapper {

	/** Traduce la lista completa de eventos */
	public List<PublicEventResponse> toPublicEventResponses(List<EventResponse> events) {
		return events.stream().map(this::toPublicEventResponse).toList();
	}

	/** Traduce un evento. No incluye el id de base de datos del microservicio, solo el internalId */
	public PublicEventResponse toPublicEventResponse(EventResponse e) {
		return new PublicEventResponse(
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
				e.sportId(),
				e.organizerInstitutionId(),
				e.organizerCampusId(),
				e.organizerTeamId(),
				e.teamOneId(),
				e.teamTwoId(),
				e.delegateOid(),
				e.delegateDisplayName());
	}

}
