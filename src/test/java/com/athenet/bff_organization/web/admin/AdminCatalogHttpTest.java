package com.athenet.bff_organization.web.admin;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.json.JsonMapper;
import com.athenet.bff_organization.client.institutions.AdminInstitutionsClient;
import com.athenet.bff_organization.config.*;
import com.athenet.bff_organization.service.admin.*;
import com.athenet.bff_organization.web.error.GlobalExceptionHandler;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringJUnitConfig(AdminCatalogHttpTest.Config.class)
@WebAppConfiguration
@TestPropertySource(properties = "athenet.cors.allowed-origins=http://localhost:4200")
class AdminCatalogHttpTest {
    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, UpstreamErrorConfig.class, AdminCatalogService.class, AdminCatalogMapper.class,
            AdminInstitutionsClient.class, AdminSportController.class, AdminInstitutionController.class,
            AdminCampusController.class, AdminTeamController.class, GlobalExceptionHandler.class,
            com.athenet.bff_organization.web.director.DirectorCompetitionController.class,
            com.athenet.bff_organization.service.director.DirectorCompetitionService.class,
            com.athenet.bff_organization.service.director.DirectorCompetitionMapper.class,
            com.athenet.bff_organization.client.competitions.CompetitionsClient.class})
    static class Config {
        @Bean JsonMapper jsonMapper() { return JsonMapper.builder().findAndAddModules().build(); }
        @Bean JwtDecoder jwtDecoder() {
            return token -> org.springframework.security.oauth2.jwt.Jwt.withTokenValue(token)
                    .header("alg", "none").subject("admin-test").claim("roles", java.util.List.of("admin")).build();
        }
        @Bean RestClient.Builder upstreamBuilder(RestClientCustomizer customizer) {
            var builder = RestClient.builder().baseUrl("http://institutions.test")
                    .requestInterceptor(new BearerTokenRelay());
            customizer.customize(builder);
            return builder;
        }
        @Bean MockRestServiceServer upstream(RestClient.Builder builder) {
            return MockRestServiceServer.bindTo(builder).build();
        }
        @Bean com.athenet.bff_organization.service.common.CatalogService catalogs() {
            return org.mockito.Mockito.mock(com.athenet.bff_organization.service.common.CatalogService.class);
        }
        @Bean RestClient competitionsRestClient(RestClient.Builder builder, MockRestServiceServer server) {
            return builder.build();
        }
        @Bean RestClient adminInstitutionsRestClient(RestClient.Builder builder, MockRestServiceServer server) {
            return builder.build();
        }
    }

    record Resource(String path, String upstream, String request, String wire, String response, String extraAssertion) {}
    static Stream<Resource> resources() {
        return Stream.of(
            new Resource("sports", "deportes", "{\"name\":\"Fútbol\",\"description\":\"Cancha\"}",
                "{\"nombre\":\"Fútbol\",\"descripcion\":\"Cancha\"}",
                "{\"id\":7,\"nombre\":\"Fútbol\",\"descripcion\":\"Cancha\"}", "name"),
            new Resource("institutions", "instituciones", "{\"name\":\"Universidad\",\"acronym\":\"UNI\",\"imageUrl\":\"logo\",\"active\":false}",
                "{\"nombre\":\"Universidad\",\"sigla\":\"UNI\",\"imagenUrl\":\"logo\",\"activo\":false}",
                "{\"id\":7,\"nombre\":\"Universidad\",\"sigla\":\"UNI\",\"imagenUrl\":\"logo\",\"activo\":false}", "acronym"),
            new Resource("campuses", "sedes", "{\"name\":\"Centro\",\"city\":\"Santiago\",\"address\":\"Calle 1\",\"institutionId\":3}",
                "{\"nombre\":\"Centro\",\"ciudad\":\"Santiago\",\"direccion\":\"Calle 1\",\"institucionId\":3}",
                "{\"id\":7,\"nombre\":\"Centro\",\"ciudad\":\"Santiago\",\"direccion\":\"Calle 1\",\"institucionId\":3,\"institucionNombre\":\"UNI\"}", "institutionId"),
            new Resource("teams", "equipos", "{\"name\":\"Halcones\",\"division\":\"MIXTO\",\"coach\":\"Ana\",\"logoUrl\":\"logo\",\"campusId\":3,\"sportId\":2}",
                "{\"nombre\":\"Halcones\",\"categoria\":\"MIXTO\",\"entrenador\":\"Ana\",\"logoUrl\":\"logo\",\"sedeId\":3,\"deporteId\":2}",
                "{\"id\":7,\"nombre\":\"Halcones\",\"categoria\":\"MIXTO\",\"entrenador\":\"Ana\",\"logoUrl\":\"logo\",\"sedeId\":3,\"sedeNombre\":\"Centro\",\"deporteId\":2,\"deporteNombre\":\"Fútbol\",\"activo\":false}", "division"));
    }

    @Autowired WebApplicationContext context;
    @Autowired MockRestServiceServer upstream;
    MockMvc mvc;
    @BeforeEach void setup() {
        upstream.reset();
        mvc = webAppContextSetup(context).apply(springSecurity()).build();
    }
    MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
        return request.with(jwt().jwt(j -> j.tokenValue("admin-token"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    String url(Resource r) { return "http://institutions.test/api/v1/" + r.upstream(); }

    @ParameterizedTest @MethodSource("resources")
    void adminCanCreateReadUpdateListAndDelete(Resource r) throws Exception {
        upstream.expect(requestTo(url(r))).andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer admin-token"))
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.content().json(r.wire()))
                .andRespond(withSuccess(r.response(), MediaType.APPLICATION_JSON));
        mvc.perform(admin(post("/api/admin/" + r.path())).contentType(MediaType.APPLICATION_JSON).content(r.request()))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/api/admin/" + r.path() + "/7"))
                .andExpect(jsonPath("$.id").value(7)).andExpect(jsonPath("$." + r.extraAssertion()).exists());
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo(url(r) + "/7")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(r.response(), MediaType.APPLICATION_JSON));
        mvc.perform(admin(get("/api/admin/" + r.path() + "/7"))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7));
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo(url(r) + "/7")).andExpect(method(HttpMethod.PUT))
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.content().json(r.wire()))
                .andRespond(withSuccess(r.response(), MediaType.APPLICATION_JSON));
        mvc.perform(admin(put("/api/admin/" + r.path() + "/7")).contentType(MediaType.APPLICATION_JSON).content(r.request()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7));
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo(url(r))).andRespond(withSuccess("[" + r.response() + "]", MediaType.APPLICATION_JSON));
        var list = mvc.perform(admin(get("/api/admin/" + r.path()))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));
        if (r.path().equals("teams") || r.path().equals("institutions")) list.andExpect(jsonPath("$[0].active").value(false));
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo(url(r) + "/7")).andExpect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", "Bearer admin-token")).andRespond(withNoContent());
        mvc.perform(admin(delete("/api/admin/" + r.path() + "/7"))).andExpect(status().isNoContent());
        upstream.verify();
    }

    @ParameterizedTest @MethodSource("resources")
    void protectedForEveryResourceAndMethod(Resource r) throws Exception {
        for (HttpMethod method : new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}) {
            String path = "/api/admin/" + r.path() + (method == HttpMethod.PUT || method == HttpMethod.DELETE ? "/7" : "");
            mvc.perform(request(method, path).contentType(MediaType.APPLICATION_JSON).content(r.request())).andExpect(status().isUnauthorized());
            for (String role : new String[]{"DIRECTOR", "DELEGATE"}) {
                mvc.perform(request(method, path).with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + role)))
                        .contentType(MediaType.APPLICATION_JSON).content(r.request())).andExpect(status().isForbidden());
            }
        }
        upstream.verify();
    }

    @Test void confirmationAndEndFieldsAreOrchestratedForDirector() throws Exception {
        var director = jwt().authorities(new SimpleGrantedAuthority("ROLE_DIRECTOR"));
        String response = "{\"id\":31,\"competitionId\":4,\"round\":\"ROUND_OF_16\",\"position\":1,"
                + "\"status\":\"SCHEDULED\",\"endDate\":\"2026-10-24\",\"endTime\":\"20:00\","
                + "\"version\":0,\"scheduleConfirmed\":true,\"scheduleVersion\":1}";
        upstream.expect(requestTo("http://institutions.test/api/admin/competitions/4/teams"))
                .andRespond(withSuccess("[]",MediaType.APPLICATION_JSON));
        upstream.expect(requestTo("http://institutions.test/api/admin/competitions/4/matches/31/confirm"))
                .andExpect(method(HttpMethod.POST)).andExpect(header("Authorization","Bearer token"))
                .andRespond(withSuccess(response,MediaType.APPLICATION_JSON));
        mvc.perform(post("/api/director/competitions/4/matches/31/confirm").with(director))
                .andExpect(status().isOk()).andExpect(jsonPath("$.scheduleConfirmed").value(true))
                .andExpect(jsonPath("$.endTime").value("20:00"));
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo("http://institutions.test/api/admin/competitions/4/teams"))
                .andRespond(withSuccess("[]",MediaType.APPLICATION_JSON));
        upstream.expect(requestTo("http://institutions.test/api/admin/competitions/4/matches/31"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath("$.endDate").value("2026-10-24"))
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath("$.endTime").value("20:00"))
                .andRespond(withSuccess(response,MediaType.APPLICATION_JSON));
        mvc.perform(put("/api/director/competitions/4/matches/31").with(director).contentType(MediaType.APPLICATION_JSON)
                .content("{\"endDate\":\"2026-10-24\",\"endTime\":\"20:00\"}"))
                .andExpect(status().isOk());
        upstream.verify();
    }

    @Test void rolesClaimIsConvertedAndActualBearerIsRelayed() throws Exception {
        upstream.expect(requestTo("http://institutions.test/api/v1/deportes"))
                .andExpect(header("Authorization", "Bearer actual-access-token"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        mvc.perform(get("/api/admin/sports").header("Authorization", "Bearer actual-access-token"))
                .andExpect(status().isOk());
        upstream.verify();
    }

    @Test void filtersAndConflictsArePreserved() throws Exception {
        upstream.expect(requestTo("http://institutions.test/api/v1/sedes?institucionId=3"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        mvc.perform(admin(get("/api/admin/campuses?institutionId=3"))).andExpect(status().isOk());
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo("http://institutions.test/api/v1/equipos?sedeId=9"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        mvc.perform(admin(get("/api/admin/teams?campusId=9"))).andExpect(status().isOk());
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo("http://institutions.test/api/v1/sedes/9"))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"La sede tiene equipos\"}"));
        mvc.perform(admin(delete("/api/admin/campuses/9"))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La sede tiene equipos"));
        upstream.verify();
    }

    @Test void validationAndUnavailableServiceAreReported() throws Exception {
        upstream.expect(requestTo("http://institutions.test/api/v1/deportes"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"El nombre es obligatorio\"}"));
        mvc.perform(admin(post("/api/admin/sports")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
        upstream.verify(); upstream.reset();
        upstream.expect(requestTo("http://institutions.test/api/v1/deportes"))
                .andRespond(withException(new java.net.SocketTimeoutException("timeout")));
        mvc.perform(admin(get("/api/admin/sports"))).andExpect(status().isServiceUnavailable());
        upstream.verify();
    }
}
