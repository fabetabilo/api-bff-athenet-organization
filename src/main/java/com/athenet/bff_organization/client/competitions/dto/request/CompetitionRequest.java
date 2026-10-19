package com.athenet.bff_organization.client.competitions.dto.request;

import java.time.LocalDate;

/**
 * CompetitionRequest corresponde al body que recibe el microservicio en POST/PUT /api/admin/competitions.
 * winPoints, drawPoints y lossPoints son opcionales: en null el microservicio aplica 3/1/0.
 */
public record CompetitionRequest(
		String name,
		String type,
		Long sportId,
		String season,
		String description,
		String competitionImg,
		LocalDate startDate,
		LocalDate endDate,
		Integer winPoints,
		Integer drawPoints,
		Integer lossPoints) {
}
