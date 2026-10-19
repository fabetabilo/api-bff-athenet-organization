package com.athenet.bff_organization.web.admin;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.athenet.bff_organization.service.admin.AdminCatalogService;
import com.athenet.bff_organization.web.admin.dto.request.AdminInstitutionRequest;
import com.athenet.bff_organization.web.admin.dto.response.AdminInstitutionResponse;

/** Gestión administrativa; SecurityConfig exige ADMIN. */
@RestController
@RequestMapping("/api/admin/institutions")
public class AdminInstitutionController {
    private final AdminCatalogService service;

    public AdminInstitutionController(AdminCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AdminInstitutionResponse> list() {
        return service.listInstitutions();
    }

    @GetMapping("/{id}")
    public AdminInstitutionResponse get(@PathVariable Long id) {
        return service.getInstitution(id);
    }

    @PostMapping
    public ResponseEntity<AdminInstitutionResponse> create(@RequestBody AdminInstitutionRequest request) {
        AdminInstitutionResponse created = service.createInstitution(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public AdminInstitutionResponse update(@PathVariable Long id, @RequestBody AdminInstitutionRequest request) {
        return service.updateInstitution(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteInstitution(id);
        return ResponseEntity.noContent().build();
    }
}
