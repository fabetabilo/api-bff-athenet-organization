package com.athenet.bff_organization.web.admin.dto.response;

public record AdminInstitutionResponse(
        Long id,
        String name,
        String acronym,
        String imageUrl,
        boolean active) {
}
