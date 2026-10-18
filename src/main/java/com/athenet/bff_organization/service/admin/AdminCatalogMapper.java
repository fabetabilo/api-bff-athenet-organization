package com.athenet.bff_organization.service.admin;

import org.springframework.stereotype.Component;
import com.athenet.bff_organization.client.institutions.dto.request.*;
import com.athenet.bff_organization.client.institutions.dto.response.*;
import com.athenet.bff_organization.web.admin.dto.request.*;
import com.athenet.bff_organization.web.admin.dto.response.*;

@Component
public class AdminCatalogMapper {
    public DeporteRequest toUpstream(AdminSportRequest r) {
        return new DeporteRequest(r.name(), r.description());
    }

    public AdminSportResponse toResponse(DeporteResponse r) {
        return new AdminSportResponse(r.id(), r.nombre(), r.descripcion());
    }

    public InstitucionRequest toUpstream(AdminInstitutionRequest r) {
        return new InstitucionRequest(r.name(), r.acronym(), r.imageUrl(), r.active());
    }

    public AdminInstitutionResponse toResponse(InstitucionResponse r) {
        return new AdminInstitutionResponse(r.id(), r.nombre(), r.sigla(), r.imagenUrl(), r.activo());
    }

    public SedeRequest toUpstream(AdminCampusRequest r) {
        return new SedeRequest(r.name(), r.city(), r.address(), r.institutionId());
    }

    public AdminCampusResponse toResponse(SedeResponse r) {
        return new AdminCampusResponse(r.id(), r.nombre(), r.ciudad(), r.direccion(), r.institucionId(), r.institucionNombre());
    }

    public EquipoRequest toUpstream(AdminTeamRequest r) {
        return new EquipoRequest(r.name(), r.division(), r.coach(), r.logoUrl(), r.campusId(), r.sportId());
    }

    public AdminTeamResponse toResponse(EquipoResponse r) {
        return new AdminTeamResponse(r.id(), r.nombre(), r.categoria(), r.entrenador(), r.logoUrl(), r.sedeId(), r.sedeNombre(), r.deporteId(), r.deporteNombre(), r.activo());
    }
}
