package com.athenet.bff_organization.web.admin;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.athenet.bff_organization.service.admin.AdminCatalogService;
import com.athenet.bff_organization.web.admin.dto.request.AdminTeamRequest;
import com.athenet.bff_organization.web.admin.dto.response.AdminTeamResponse;

/** Gestión administrativa; SecurityConfig exige ADMIN. */
@RestController
@RequestMapping("/api/admin/teams")
public class AdminTeamController {
    private final AdminCatalogService service;

    public AdminTeamController(AdminCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminTeamResponse> list(@RequestParam(required = false) Long campusId) {
        return service.listTeams(campusId);
    }

    @GetMapping("/{id}")
    public AdminTeamResponse get(@PathVariable Long id) {
        return service.getTeam(id);
    }

    @PostMapping
    public ResponseEntity<AdminTeamResponse> create(@RequestBody AdminTeamRequest request) {
        AdminTeamResponse created = service.createTeam(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public AdminTeamResponse update(@PathVariable Long id, @RequestBody AdminTeamRequest request) {
        return service.updateTeam(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
