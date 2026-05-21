package br.com.kollectaops.api.picking.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String token;
    private long expiresIn;
    private UsuarioDto usuario;
    private List<FilialDto> filiais;

    @Data @Builder
    public static class UsuarioDto {
        private Long id;
        private String nome;
        private String codigoCracha;
        private String perfil;
    }

    @Data @Builder
    public static class FilialDto {
        private Long id;
        private String codigo;
        private String nome;
    }
}
