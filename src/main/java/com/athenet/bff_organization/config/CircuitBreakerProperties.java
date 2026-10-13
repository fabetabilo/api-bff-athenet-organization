package com.athenet.bff_organization.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion comun de los circuit breakers (uno por microservicio). Se evaluan las ultimas
 * slidingWindowSize llamadas: con al menos minimumNumberOfCalls y failureRateThreshold % de fallas, el
 * circuito se abre durante waitDurationInOpenState y luego deja pasar permittedCallsInHalfOpenState de prueba.
 */
@ConfigurationProperties("athenet.resilience.circuit-breaker")
public record CircuitBreakerProperties(
		int slidingWindowSize,
		int minimumNumberOfCalls,
		float failureRateThreshold,
		Duration waitDurationInOpenState,
		int permittedCallsInHalfOpenState) {
}
