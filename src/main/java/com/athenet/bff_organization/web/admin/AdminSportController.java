package com.athenet.bff_organization.web.admin;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.athenet.bff_organization.service.admin.AdminCatalogService;
import com.athenet.bff_organization.web.admin.dto.request.AdminSportRequest;
import com.athenet.bff_organization.web.admin.dto.response.AdminSportResponse;

/** Gestión administrativa; SecurityConfig exige ADMIN. */
@RestController
@RequestMapping("/api/admin/sports")
public class AdminSportController {
    private final AdminCatalogService service;

    public AdminSportController(AdminCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminSportResponse> list() {
        return service.listSports();
    }

    @GetMapping("/{id}")
    public AdminSportResponse get(@PathVariable Long id) {
        return service.getSport(id);
    }

    @PostMapping
    public ResponseEntity<AdminSportResponse> create(@RequestBody AdminSportRequest request) {
        AdminSportResponse created = service.createSport(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public AdminSportResponse update(@PathVariable Long id, @RequestBody AdminSportRequest request) {
        return service.updateSport(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSport(id);
        return ResponseEntity.noContent().build();
    }
}
