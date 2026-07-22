package com.mehmetserin.iban.web;

import com.mehmetserin.iban.service.BicIbanService;
import com.mehmetserin.iban.service.BicIbanService.BicResult;
import com.mehmetserin.iban.service.BicIbanService.IbanResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BicIbanController {

    public record ValidateRequest(@NotBlank @Size(max = 128) String value) {}

    private final BicIbanService service;

    public BicIbanController(BicIbanService service) {
        this.service = service;
    }

    @PostMapping("/iban/validate")
    public ResponseEntity<IbanResult> validateIban(@Valid @RequestBody ValidateRequest request) {
        IbanResult result = service.validateIban(request.value());
        return result.valid() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/bic/validate")
    public ResponseEntity<BicResult> validateBic(@Valid @RequestBody ValidateRequest request) {
        BicResult result = service.validateBic(request.value());
        return result.valid() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "healthy", "service", "BicIbanToolkit");
    }
}
