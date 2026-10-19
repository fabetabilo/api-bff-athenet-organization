package com.athenet.bff_organization.service.admin;

import java.util.List;
import org.springframework.stereotype.Service;
import com.athenet.bff_organization.client.institutions.AdminInstitutionsClient;
import com.athenet.bff_organization.web.admin.dto.request.*;
import com.athenet.bff_organization.web.admin.dto.response.*;

/** Conserva reglas de negocio y restricciones de borrado en MS-Instituciones. */
@Service
public class AdminCatalogService {
    private final AdminInstitutionsClient client;
    private final AdminCatalogMapper mapper;

    public AdminCatalogService(AdminInstitutionsClient client, AdminCatalogMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }


    public List<AdminSportResponse> listSports() {
        return client.listSports().stream().map(mapper::toResponse).toList();
    }

    public AdminSportResponse getSport(Long id) {
        return mapper.toResponse(client.getSport(id));
    }

    public AdminSportResponse createSport(AdminSportRequest request) {
        return mapper.toResponse(client.createSport(mapper.toUpstream(request)));
    }

    public AdminSportResponse updateSport(Long id, AdminSportRequest request) {
        return mapper.toResponse(client.updateSport(id, mapper.toUpstream(request)));
    }

    public void deleteSport(Long id) {
        client.deleteSport(id);
    }

    public List<AdminInstitutionResponse> listInstitutions() {
        return client.listInstitutions().stream().map(mapper::toResponse).toList();
    }

    public AdminInstitutionResponse getInstitution(Long id) {
        return mapper.toResponse(client.getInstitution(id));
    }

    public AdminInstitutionResponse createInstitution(AdminInstitutionRequest request) {
        return mapper.toResponse(client.createInstitution(mapper.toUpstream(request)));
    }

    public AdminInstitutionResponse updateInstitution(Long id, AdminInstitutionRequest request) {
        return mapper.toResponse(client.updateInstitution(id, mapper.toUpstream(request)));
    }

    public void deleteInstitution(Long id) {
        client.deleteInstitution(id);
    }

    public List<AdminCampusResponse> listCampuses(Long institutionId) {
        return client.listCampuses(institutionId).stream().map(mapper::toResponse).toList();
    }

    public AdminCampusResponse getCampus(Long id) {
        return mapper.toResponse(client.getCampus(id));
    }

    public AdminCampusResponse createCampus(AdminCampusRequest request) {
        return mapper.toResponse(client.createCampus(mapper.toUpstream(request)));
    }

    public AdminCampusResponse updateCampus(Long id, AdminCampusRequest request) {
        return mapper.toResponse(client.updateCampus(id, mapper.toUpstream(request)));
    }

    public void deleteCampus(Long id) {
        client.deleteCampus(id);
    }

    public List<AdminTeamResponse> listTeams(Long campusId) {
        return client.listTeams(campusId).stream().map(mapper::toResponse).toList();
    }

    public AdminTeamResponse getTeam(Long id) {
        return mapper.toResponse(client.getTeam(id));
    }

    public AdminTeamResponse createTeam(AdminTeamRequest request) {
        return mapper.toResponse(client.createTeam(mapper.toUpstream(request)));
    }

    public AdminTeamResponse updateTeam(Long id, AdminTeamRequest request) {
        return mapper.toResponse(client.updateTeam(id, mapper.toUpstream(request)));
    }

    public void deleteTeam(Long id) {
        client.deleteTeam(id);
    }
}
