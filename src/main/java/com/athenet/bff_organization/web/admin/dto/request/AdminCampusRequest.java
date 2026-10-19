package com.athenet.bff_organization.web.admin.dto.request;

public record AdminCampusRequest(
        String name,
        String city,
        String address,
        Long institutionId) {
}
