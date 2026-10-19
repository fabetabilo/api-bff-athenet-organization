package com.athenet.bff_organization.client.competitions.dto.request;

/** Body de POST /api/admin/competitions/{id}/teams */
public record EnrollmentRequest(
		Long teamId) {
}
