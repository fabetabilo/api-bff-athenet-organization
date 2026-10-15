package com.athenet.bff_organization.client.events.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** EventResponse corresponde a la respuesta que entrega el microservicio */
public record EventResponse(
		Long id,
		String internalId,
		String title,
		String description,
		String descriptionOpt,
		String type,
		String status,
		Boolean isOfficial,
		LocalDate eventDate,
		LocalTime startTime,
		LocalDate endDate,
		LocalTime endTime,
		String location,
		String address,
		String coverImage,
		List<String> photos,
		Long sportId,
		Long organizerInstitutionId,
		Long organizerCampusId,
		Long organizerTeamId,
		Long teamOneId,
		Long teamTwoId,
		String delegateOid,
		String delegateDisplayName,
		Long competitionId,
		Long competitionMatchId) {
}
