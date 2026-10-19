package com.athenet.bff_organization.client.institutions;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.athenet.bff_organization.client.institutions.dto.response.DeporteResponse;
import com.athenet.bff_organization.client.institutions.dto.response.EquipoResponse;
import com.athenet.bff_organization.client.institutions.dto.response.InstitucionResponse;
import com.athenet.bff_organization.client.institutions.dto.response.SedeResponse;

/** Lectura de ms-institutions. Todos sus GET son publicos: no se reenvia token (ver RestClientConfig) */
@Component
public class InstitutionsClient {

	private final RestClient restClient;

	public InstitutionsClient(@Qualifier("institutionsRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public List<DeporteResponse> getDeportes() {
		return restClient.get()
				.uri("/api/v1/deportes")
				.retrieve()
				.body(new ParameterizedTypeReference<List<DeporteResponse>>() {});
	}

	/** Incluye las inactivas */
	public List<InstitucionResponse> getInstituciones() {
		return restClient.get()
				.uri("/api/v1/instituciones")
				.retrieve()
				.body(new ParameterizedTypeReference<List<InstitucionResponse>>() {});
	}

	public List<SedeResponse> getSedes() {
		return restClient.get()
				.uri("/api/v1/sedes")
				.retrieve()
				.body(new ParameterizedTypeReference<List<SedeResponse>>() {});
	}

	/** Lista vacia si la institucion no existe o no tiene sedes */
	public List<SedeResponse> getSedesByInstitucion(Long institucionId) {
		return restClient.get()
				.uri("/api/v1/sedes?institucionId={institucionId}", institucionId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<SedeResponse>>() {});
	}

	/** Incluye los inactivos */
	public List<EquipoResponse> getEquipos() {
		return restClient.get()
				.uri("/api/v1/equipos")
				.retrieve()
				.body(new ParameterizedTypeReference<List<EquipoResponse>>() {});
	}

	/** Incluye los inactivos. Lista vacia si la sede no existe o no tiene equipos */
	public List<EquipoResponse> getEquiposBySede(Long sedeId) {
		return restClient.get()
				.uri("/api/v1/equipos?sedeId={sedeId}", sedeId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<EquipoResponse>>() {});
	}

}
