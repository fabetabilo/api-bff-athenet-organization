package com.athenet.bff_organization.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

/** Registry de circuit breakers: cada RestClient toma el suyo por nombre de microservicio (ver RestClientConfig) */
@Slf4j
@Configuration
@EnableConfigurationProperties(CircuitBreakerProperties.class)
public class ResilienceConfig {

	@Bean
	CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerProperties properties) {
		CircuitBreakerConfig config = CircuitBreakerConfig.custom()
				.slidingWindowType(SlidingWindowType.COUNT_BASED)
				.slidingWindowSize(properties.slidingWindowSize())
				.minimumNumberOfCalls(properties.minimumNumberOfCalls())
				.failureRateThreshold(properties.failureRateThreshold())
				.waitDurationInOpenState(properties.waitDurationInOpenState())
				.permittedNumberOfCallsInHalfOpenState(properties.permittedCallsInHalfOpenState())
				.build();

		CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
		// Cada circuito nuevo deja en el log sus cambios de estado (CLOSED → OPEN → HALF_OPEN → CLOSED)
		registry.getEventPublisher().onEntryAdded(added -> added.getAddedEntry().getEventPublisher()
				.onStateTransition(event -> log.warn("Circuit breaker {}: {}",
						event.getCircuitBreakerName(), event.getStateTransition())));
		return registry;
	}

}
