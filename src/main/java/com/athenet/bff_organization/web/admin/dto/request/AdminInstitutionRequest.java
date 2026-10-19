package com.athenet.bff_organization.web.admin.dto.request;

public record AdminInstitutionRequest(
        String name,
        String acronym,
        String imageUrl,
        boolean active) {
}
