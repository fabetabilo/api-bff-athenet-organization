package com.athenet.bff_organization.web.admin.dto.response;

public record AdminCampusResponse(
        Long id,
        String name,
        String city,
        String address,
        Long institutionId,
        String institutionName) {
}
