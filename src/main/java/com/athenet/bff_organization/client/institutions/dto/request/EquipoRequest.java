package com.athenet.bff_organization.client.institutions.dto.request;

/** Contrato de escritura de MS-Instituciones. */
public record EquipoRequest(
        String nombre,
        String categoria,
        String entrenador,
        String logoUrl,
        Long sedeId,
        Long deporteId) {
}
