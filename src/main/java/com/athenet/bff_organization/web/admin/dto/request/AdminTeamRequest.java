package com.athenet.bff_organization.web.admin.dto.request;

public record AdminTeamRequest(
        String name,
        String division,
        String coach,
        String logoUrl,
        Long campusId,
        Long sportId) {
}
