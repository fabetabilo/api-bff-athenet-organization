package com.athenet.bff_organization.web.admin.dto.response;

public record AdminTeamResponse(
        Long id,
        String name,
        String division,
        String coach,
        String logoUrl,
        Long campusId,
        String campusName,
        Long sportId,
        String sportName,
        boolean active) {
}
