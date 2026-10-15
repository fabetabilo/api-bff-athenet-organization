package com.athenet.bff_organization.client.competitions.dto.request;

/** Body de PATCH /api/admin/competitions/{id}/status. status: DRAFT, IN_PROGRESS o FINISHED */
public record CompetitionStatusRequest(
		String status) {
}
