package com.athenet.bff_organization.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import tools.jackson.databind.json.JsonMapper;
import com.athenet.bff_organization.client.competitions.CompetitionsClient;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;
import com.athenet.bff_organization.client.events.EventsClient;
import com.athenet.bff_organization.client.events.dto.response.EventResponse;
import com.athenet.bff_organization.client.institutions.dto.response.*;
import com.athenet.bff_organization.service.common.*;
import com.athenet.bff_organization.service.delegate.*;
import com.athenet.bff_organization.service.director.*;
import com.athenet.bff_organization.service.exception.ServiceException;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.delegate.dto.request.DelegateResultRequest;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class OrganizationFlowTest {
    final JsonMapper json = JsonMapper.builder().findAndAddModules().build();
    final EventsClient events = mock(EventsClient.class);
    final CatalogService catalogs = mock(CatalogService.class);
    final CompetitionsClient competitions = mock(CompetitionsClient.class);
    final EventReferenceValidator validator = new EventReferenceValidator();
    final DirectorEventService director = new DirectorEventService(events, catalogs, new DirectorEventMapper(), validator);

    DirectorEventRequest request(String overrides) throws Exception {
        return json.readValue("{\"internalId\":\"EVT-1\",\"type\":\"TRACKDAY\",\"organizerInstitutionId\":1,"
                + "\"organizerCampusId\":2,\"organizerTeamId\":3,\"sportId\":4" + overrides + "}", DirectorEventRequest.class);
    }
    InstitutionsSnapshot refs() {
        return InstitutionsSnapshot.of(List.of(new DeporteResponse(4L,"Fútbol",null)),
                List.of(new InstitucionResponse(1L,"UNI","U",null,true)),
                List.of(new SedeResponse(2L,"Centro","Santiago","calle",1L,"UNI")),
                List.of(new EquipoResponse(3L,"Halcones","MIXTO",null,null,2L,"Centro",4L,"Fútbol",true)));
    }
    @Test void validReferencesAreAcceptedAndWrongRelationsRejectedBeforeWrite() throws Exception {
        assertThatCode(() -> validator.validate(request(""), refs())).doesNotThrowAnyException();
        when(catalogs.getInstitutionsSnapshot()).thenReturn(refs());
        var bad = json.readValue("{\"type\":\"TRACKDAY\",\"organizerInstitutionId\":9,\"organizerCampusId\":2}", DirectorEventRequest.class);
        assertThatThrownBy(() -> director.createEvent(bad)).isInstanceOfSatisfying(ServiceException.class,
                ex -> assertThat(ex.getFieldErrors()).extracting(ServiceException.FieldDetail::field)
                        .contains("organizerInstitutionId", "organizerCampusId"));
        verifyNoInteractions(events);
    }
    @Test void wrongSportOrUnknownTeamIsRejected() throws Exception {
        var wrongSport = json.readValue("{\"type\":\"TRACKDAY\",\"organizerInstitutionId\":1,\"organizerCampusId\":2,\"organizerTeamId\":3,\"sportId\":99}", DirectorEventRequest.class);
        assertThatThrownBy(() -> validator.validate(wrongSport, refs())).isInstanceOf(ServiceException.class);
        var unknown = json.readValue("{\"type\":\"TRACKDAY\",\"organizerInstitutionId\":1,\"organizerCampusId\":2,\"organizerTeamId\":99}", DirectorEventRequest.class);
        assertThatThrownBy(() -> validator.validate(unknown, refs())).isInstanceOf(ServiceException.class);
    }
    @Test void competitionOriginCannotBeBypassedByChangingRequestTypeOrDeleting() throws Exception {
        when(events.getEvent("EVT-1")).thenReturn(json.readValue(
                "{\"internalId\":\"EVT-1\",\"type\":\"MATCH\",\"competitionId\":8,\"competitionMatchId\":9}", EventResponse.class));
        var nonMatchRequest = request("");
        assertThatThrownBy(() -> director.updateEvent("EVT-1", nonMatchRequest)).isInstanceOf(ServiceException.class);
        assertThatThrownBy(() -> director.deleteEvent("EVT-1")).isInstanceOf(ServiceException.class);
        verify(events, never()).updateEvent(anyString(), any());
        verify(events, never()).deleteEvent(anyString());
    }
    @Test void failedEnrichmentDoesNotHideSavedResult() throws Exception {
        var match = new MatchResponse(9L,8L,"FINAL",1,null,null,null,null,3L,5L,"oid","Delegado","FINISHED",2,1,1,null,null,true,1);
        when(competitions.registerResult(eq(9L), any())).thenReturn(match);
        when(competitions.getPublicCompetitions()).thenThrow(new ResourceAccessException("timeout"));
        when(catalogs.getInstitutionsSnapshotOrEmpty()).thenReturn(InstitutionsSnapshot.empty());
        var service = new DelegateMatchService(competitions,catalogs,new DelegateMatchMapper());
        var result = service.registerResult(9L,new DelegateResultRequest(2,1));
        assertThat(result.status()).isEqualTo("FINISHED");
        assertThat(result.competition().id()).isEqualTo(8L);
        assertThat(result.competition().name()).isNull();
        assertThat(result.homeScore()).isEqualTo(2);
        verify(competitions).registerResult(eq(9L),any());
    }
    @Test void matchUpdateLoadsRosterBeforeWrite() {
        when(competitions.getTeams(8L)).thenThrow(new ResourceAccessException("timeout"));
        var service = new DirectorCompetitionService(competitions,catalogs,new DirectorCompetitionMapper());
        assertThatThrownBy(() -> service.updateMatch(8L,9L,null)).isInstanceOf(ResourceAccessException.class);
        verify(competitions,never()).updateMatch(anyLong(),anyLong(),any());
    }
}
