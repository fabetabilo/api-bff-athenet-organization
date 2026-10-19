package com.athenet.bff_organization.client.institutions.dto.request;

/** Contrato de escritura de MS-Instituciones. */
public record InstitucionRequest(
        String nombre,
        String sigla,
        String imagenUrl,
        boolean activo) {
}
