package com.athenet.bff_organization.client.institutions.dto.request;

/** Contrato de escritura de MS-Instituciones. */
public record SedeRequest(
        String nombre,
        String ciudad,
        String direccion,
        Long institucionId) {
}
