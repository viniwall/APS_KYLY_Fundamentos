package br.com.kollectaops.api.picking.controller;

import br.com.kollectaops.api.picking.dto.LoginRequest;
import br.com.kollectaops.api.picking.dto.LoginResponse;
import br.com.kollectaops.api.picking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login por bipagem de crachá")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login via bipagem de crachá do supervisor + operador")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    @Operation(summary = "Encerra sessão atual")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
