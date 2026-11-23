package com.athenet.bff_organization.web.delegate;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.athenet.bff_organization.service.delegate.DelegateMatchService;
import com.athenet.bff_organization.web.delegate.dto.request.DelegateLiveScoreRequest;
import com.athenet.bff_organization.web.delegate.dto.request.DelegateResultRequest;
import com.athenet.bff_organization.web.delegate.dto.response.DelegateMatchResponse;

/** "Mis eventos" del delegado: iniciar, finalizar y corregir sus partidos (rol DELEGATE, ver SecurityConfig) */
@RestController
@RequestMapping("/api/delegate/matches")
public class DelegateMatchController {

	private final DelegateMatchService delegateMatchService;

	public DelegateMatchController(DelegateMatchService delegateMatchService) {
		this.delegateMatchService = delegateMatchService;
	}

	@GetMapping
	public List<DelegateMatchResponse> getMatches() {
		return delegateMatchService.getMatches();
	}

	@PostMapping("/{matchId}/start")
	public DelegateMatchResponse startMatch(@PathVariable Long matchId) {
		return delegateMatchService.startMatch(matchId);
	}

	/** Publica el marcador en vivo en ms-competiciones (202 Accepted, sin escribir en BD) */
	@PostMapping("/{matchId}/score")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void publishLiveScore(@PathVariable Long matchId,
			@RequestBody DelegateLiveScoreRequest request) {
		delegateMatchService.publishLiveScore(matchId, request);
	}

	/** Finaliza el partido con su resultado o lo corrige si ya estaba finalizado */
	@PutMapping("/{matchId}/result")
	public DelegateMatchResponse registerResult(@PathVariable Long matchId,
			@RequestBody DelegateResultRequest request) {
		return delegateMatchService.registerResult(matchId, request);
	}

}
