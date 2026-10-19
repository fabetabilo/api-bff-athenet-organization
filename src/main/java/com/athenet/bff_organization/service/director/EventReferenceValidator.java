package com.athenet.bff_organization.service.director;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import com.athenet.bff_organization.service.common.InstitutionsSnapshot;
import com.athenet.bff_organization.service.exception.ServiceException;
import com.athenet.bff_organization.service.exception.ServiceException.FieldDetail;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;

/** Valida referencias externas antes de escribir; las reglas del evento siguen en Eventos. */
@Component
public class EventReferenceValidator {
    public void validate(DirectorEventRequest r, InstitutionsSnapshot refs) {
        List<FieldDetail> errors = new ArrayList<>();
        if (r.sportId() != null && refs.deporte(r.sportId()) == null) {
            errors.add(error("sportId", "NOT_FOUND", "El deporte no existe."));
        }
        if (!"MATCH".equals(r.type())) {
            var institution = refs.institucion(r.organizerInstitutionId());
            var campus = refs.sede(r.organizerCampusId());
            var team = refs.equipo(r.organizerTeamId());
            if (institution == null) {
                errors.add(error("organizerInstitutionId", "NOT_FOUND", "La institución no existe."));
            }
            if (campus == null) {
                errors.add(error("organizerCampusId", "NOT_FOUND", "La sede no existe."));
            } else if (!Objects.equals(campus.institucionId(), r.organizerInstitutionId())) {
                errors.add(error("organizerCampusId", "INVALID_RELATION", "La sede no pertenece a la institución."));
            }
            if (r.organizerTeamId() != null) {
                if (team == null) {
                    errors.add(error("organizerTeamId", "NOT_FOUND", "El equipo no existe."));
                } else {
                    if (!Objects.equals(team.sedeId(), r.organizerCampusId())) {
                        errors.add(error("organizerTeamId", "INVALID_RELATION", "El equipo no pertenece a la sede."));
                    }
                    if (r.sportId() != null && !Objects.equals(team.deporteId(), r.sportId())) {
                        errors.add(error("sportId", "INVALID_RELATION", "El deporte no corresponde al equipo."));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ServiceException(ServiceException.Type.VALIDATION_FAILED, "INVALID_REFERENCES",
                    "Las referencias del evento no son válidas.", errors);
        }
    }

    private static FieldDetail error(String field, String code, String message) {
        return new FieldDetail(field, code, message);
    }
}
