package com.athenet.bff_organization.client.institutions;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.athenet.bff_organization.client.institutions.dto.request.*;
import com.athenet.bff_organization.client.institutions.dto.response.*;

/** Cliente administrativo con relay JWT, separado de las consultas públicas. */
@Component
public class AdminInstitutionsClient {
    private final RestClient restClient;

    public AdminInstitutionsClient(@Qualifier("adminInstitutionsRestClient") RestClient restClient) {
        this.restClient = restClient;
    }


    public List<DeporteResponse> listSports() {
        return restClient.get().uri("/api/v1/deportes").retrieve()
                .body(new ParameterizedTypeReference<List<DeporteResponse>>() {});
    }

    public DeporteResponse getSport(Long id) {
        return restClient.get().uri("/api/v1/deportes/{id}", id).retrieve().body(DeporteResponse.class);
    }

    public DeporteResponse createSport(DeporteRequest request) {
        return restClient.post().uri("/api/v1/deportes").body(request).retrieve().body(DeporteResponse.class);
    }

    public DeporteResponse updateSport(Long id, DeporteRequest request) {
        return restClient.put().uri("/api/v1/deportes/{id}", id).body(request).retrieve().body(DeporteResponse.class);
    }

    public void deleteSport(Long id) {
        restClient.delete().uri("/api/v1/deportes/{id}", id).retrieve().toBodilessEntity();
    }

    public List<InstitucionResponse> listInstitutions() {
        return restClient.get().uri("/api/v1/instituciones").retrieve()
                .body(new ParameterizedTypeReference<List<InstitucionResponse>>() {});
    }

    public InstitucionResponse getInstitution(Long id) {
        return restClient.get().uri("/api/v1/instituciones/{id}", id).retrieve().body(InstitucionResponse.class);
    }

    public InstitucionResponse createInstitution(InstitucionRequest request) {
        return restClient.post().uri("/api/v1/instituciones").body(request).retrieve().body(InstitucionResponse.class);
    }

    public InstitucionResponse updateInstitution(Long id, InstitucionRequest request) {
        return restClient.put().uri("/api/v1/instituciones/{id}", id).body(request).retrieve().body(InstitucionResponse.class);
    }

    public void deleteInstitution(Long id) {
        restClient.delete().uri("/api/v1/instituciones/{id}", id).retrieve().toBodilessEntity();
    }

    public List<SedeResponse> listCampuses(Long filter) {
        return restClient.get().uri(filter == null ? "/api/v1/sedes" : "/api/v1/sedes?institucionId={filter}", filter).retrieve()
                .body(new ParameterizedTypeReference<List<SedeResponse>>() {});
    }

    public SedeResponse getCampus(Long id) {
        return restClient.get().uri("/api/v1/sedes/{id}", id).retrieve().body(SedeResponse.class);
    }

    public SedeResponse createCampus(SedeRequest request) {
        return restClient.post().uri("/api/v1/sedes").body(request).retrieve().body(SedeResponse.class);
    }

    public SedeResponse updateCampus(Long id, SedeRequest request) {
        return restClient.put().uri("/api/v1/sedes/{id}", id).body(request).retrieve().body(SedeResponse.class);
    }

    public void deleteCampus(Long id) {
        restClient.delete().uri("/api/v1/sedes/{id}", id).retrieve().toBodilessEntity();
    }

    public List<EquipoResponse> listTeams(Long filter) {
        return restClient.get().uri(filter == null ? "/api/v1/equipos" : "/api/v1/equipos?sedeId={filter}", filter).retrieve()
                .body(new ParameterizedTypeReference<List<EquipoResponse>>() {});
    }

    public EquipoResponse getTeam(Long id) {
        return restClient.get().uri("/api/v1/equipos/{id}", id).retrieve().body(EquipoResponse.class);
    }

    public EquipoResponse createTeam(EquipoRequest request) {
        return restClient.post().uri("/api/v1/equipos").body(request).retrieve().body(EquipoResponse.class);
    }

    public EquipoResponse updateTeam(Long id, EquipoRequest request) {
        return restClient.put().uri("/api/v1/equipos/{id}", id).body(request).retrieve().body(EquipoResponse.class);
    }

    public void deleteTeam(Long id) {
        restClient.delete().uri("/api/v1/equipos/{id}", id).retrieve().toBodilessEntity();
    }
}
