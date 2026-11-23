package com.athenet.bff_organization.web.delegate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.springframework.test.web.client.match.MockRestRequestMatchers;

import java.net.SocketTimeoutException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
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

import com.athenet.bff_organization.client.competitions.CompetitionsClient;
import com.athenet.bff_organization.config.BearerTokenRelay;
import com.athenet.bff_organization.config.SecurityConfig;
import com.athenet.bff_organization.config.UpstreamErrorConfig;
import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.service.delegate.DelegateMatchMapper;
import com.athenet.bff_organization.service.delegate.DelegateMatchService;
import com.athenet.bff_organization.web.error.GlobalExceptionHandler;

import tools.jackson.databind.json.JsonMapper;

@SpringJUnitConfig(DelegateLiveScoreHttpTest.Config.class)
@WebAppConfiguration
@TestPropertySource(properties = "athenet.cors.allowed-origins=http://localhost:4200")
class DelegateLiveScoreHttpTest {

	@Configuration
	@EnableWebMvc
	@Import({
			SecurityConfig.class,
			UpstreamErrorConfig.class,
			CompetitionsClient.class,
			DelegateMatchMapper.class,
			DelegateMatchService.class,
			DelegateMatchController.class,
			GlobalExceptionHandler.class
	})
	static class Config {
		@Bean JsonMapper jsonMapper() { return JsonMapper.builder().findAndAddModules().build(); }
		@Bean JwtDecoder jwtDecoder() {
			return token -> Jwt.withTokenValue(token)
					.header("alg", "none").subject("delegate-test").claim("roles", List.of("delegate")).build();
		}
		@Bean CatalogService catalogService() { return Mockito.mock(CatalogService.class); }
		@Bean RestClient.Builder upstreamBuilder(RestClientCustomizer customizer) {
			var builder = RestClient.builder().baseUrl("http://competitions.test")
					.requestInterceptor(new BearerTokenRelay());
			customizer.customize(builder);
			return builder;
		}
		@Bean MockRestServiceServer upstream(RestClient.Builder builder) {
			return MockRestServiceServer.bindTo(builder).build();
		}
		@Bean RestClient competitionsRestClient(RestClient.Builder builder, MockRestServiceServer server) {
			return builder.build();
		}
		@Bean RestClient competitionsLiveRestClient(RestClient.Builder builder, MockRestServiceServer server) {
			return builder.build();
		}
	}

	@Autowired WebApplicationContext context;
	@Autowired MockRestServiceServer upstream;
	MockMvc mvc;

	@BeforeEach
	void setup() {
		upstream.reset();
		mvc = webAppContextSetup(context).apply(springSecurity()).build();
	}

	private MockHttpServletRequestBuilder delegate(MockHttpServletRequestBuilder request) {
		return request.with(jwt().jwt(j -> j.tokenValue("delegate-access-token"))
				.authorities(new SimpleGrantedAuthority("ROLE_DELEGATE")));
	}

	@Test
	void forwardsLiveScoreWithDelegateBearerAndReturns202() throws Exception {
		upstream.expect(requestTo("http://competitions.test/api/delegate/matches/31/score"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Authorization", "Bearer delegate-access-token"))
				.andExpect(MockRestRequestMatchers.content().json("{\"homeScore\":2,\"awayScore\":1}"))
				.andRespond(withStatus(HttpStatus.ACCEPTED));

		mvc.perform(delegate(post("/api/delegate/matches/31/score"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"homeScore\":2,\"awayScore\":1}"))
				.andExpect(status().isAccepted());

		upstream.verify();
	}

	@Test
	void rejectsUnauthenticatedOrWrongRoleWithoutCallingUpstream() throws Exception {
		mvc.perform(post("/api/delegate/matches/31/score")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"homeScore\":1,\"awayScore\":0}"))
				.andExpect(status().isUnauthorized());

		for (String role : new String[] { "DIRECTOR", "ADMIN" }) {
			mvc.perform(post("/api/delegate/matches/31/score")
							.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_" + role)))
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"homeScore\":1,\"awayScore\":0}"))
					.andExpect(status().isForbidden());
		}

		upstream.verify();
	}

	@Test
	void propagates403WhenDelegateIsNotAssigned() throws Exception {
		upstream.expect(requestTo("http://competitions.test/api/delegate/matches/31/score"))
				.andRespond(withStatus(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
						.body("{\"message\":\"Solo el delegado asignado puede gestionar el partido 31\"}"));

		mvc.perform(delegate(post("/api/delegate/matches/31/score"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"homeScore\":1,\"awayScore\":0}"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("Solo el delegado asignado puede gestionar el partido 31"));

		upstream.verify();
	}

	@Test
	void propagates409WhenMatchIsNotInProgress() throws Exception {
		upstream.expect(requestTo("http://competitions.test/api/delegate/matches/31/score"))
				.andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
						.body("{\"message\":\"Solo se puede actualizar el marcador de un partido en curso\"}"));

		mvc.perform(delegate(post("/api/delegate/matches/31/score"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"homeScore\":1,\"awayScore\":0}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Solo se puede actualizar el marcador de un partido en curso"));

		upstream.verify();
	}

	@Test
	void propagates503WhenKafkaOrCompetitionsIsUnavailable() throws Exception {
		upstream.expect(requestTo("http://competitions.test/api/delegate/matches/31/score"))
				.andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE).contentType(MediaType.APPLICATION_JSON)
						.body("{\"message\":\"No se pudo publicar el marcador en vivo. Intenta nuevamente.\"}"));

		mvc.perform(delegate(post("/api/delegate/matches/31/score"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"homeScore\":1,\"awayScore\":0}"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.message").value("No se pudo publicar el marcador en vivo. Intenta nuevamente."));
		upstream.verify();
		upstream.reset();

		upstream.expect(requestTo("http://competitions.test/api/delegate/matches/31/score"))
				.andRespond(withException(new SocketTimeoutException("timeout")));

		mvc.perform(delegate(post("/api/delegate/matches/31/score"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"homeScore\":1,\"awayScore\":0}"))
				.andExpect(status().isServiceUnavailable());
		upstream.verify();
	}
}
