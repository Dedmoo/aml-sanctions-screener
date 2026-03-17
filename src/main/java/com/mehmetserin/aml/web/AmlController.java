package com.mehmetserin.aml.web;

import com.mehmetserin.aml.service.SanctionsScreeningService;
import com.mehmetserin.aml.service.SanctionsScreeningService.ScreenResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/aml")
public class AmlController {

    public record ScreenRequest(@NotBlank String name) {}

    private final SanctionsScreeningService service;

    public AmlController(SanctionsScreeningService service) {
        this.service = service;
    }

    @PostMapping("/screen")
    public ScreenResult screen(@Valid @RequestBody ScreenRequest request) {
        return service.screen(request.name());
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "healthy", "service", "aml-sanctions-screener");
    }
}
