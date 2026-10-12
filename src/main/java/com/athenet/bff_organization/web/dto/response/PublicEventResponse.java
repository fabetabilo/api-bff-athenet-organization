package com.athenet.bff_organization.web.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/** Repuesta que se entrega a la web frontend */
public record PublicEventResponse(
		String internalId,
		String title,
		String description,
		String descriptionOpt,
		String type,
		String status,
		Boolean isOfficial,
		LocalDate eventDate,
		@JsonFormat(pattern = "HH:mm") LocalTime startTime,
		LocalDate endDate,
		@JsonFormat(pattern = "HH:mm") LocalTime endTime,
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
		String delegateDisplayName) {
}
