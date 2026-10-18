package com.athenet.bff_organization.web.admin;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.athenet.bff_organization.service.admin.AdminCatalogService;
import com.athenet.bff_organization.web.admin.dto.request.AdminCampusRequest;
import com.athenet.bff_organization.web.admin.dto.response.AdminCampusResponse;

/** Gestión administrativa; SecurityConfig exige ADMIN. */
@RestController
@RequestMapping("/api/admin/campuses")
public class AdminCampusController {
    private final AdminCatalogService service;

    public AdminCampusController(AdminCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminCampusResponse> list(@RequestParam(required = false) Long institutionId) {
        return service.listCampuses(institutionId);
    }

    @GetMapping("/{id}")
    public AdminCampusResponse get(@PathVariable Long id) {
        return service.getCampus(id);
    }

    @PostMapping
    public ResponseEntity<AdminCampusResponse> create(@RequestBody AdminCampusRequest request) {
        AdminCampusResponse created = service.createCampus(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public AdminCampusResponse update(@PathVariable Long id, @RequestBody AdminCampusRequest request) {
        return service.updateCampus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteCampus(id);
        return ResponseEntity.noContent().build();
    }
}
