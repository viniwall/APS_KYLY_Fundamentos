package br.com.kollectaops.api.common.exception;

import br.com.kollectaops.api.common.dto.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ProblemDetail.builder()
                .type("about:blank")
                .title("Not Found")
                .status(404)
                .detail(ex.getMessage())
                .instance(req.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
            ProblemDetail.builder()
                .type("about:blank")
                .title("Business Rule Violation")
                .status(422)
                .detail(ex.getMessage())
                .instance(req.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ProblemDetail.builder()
                .type("about:blank")
                .title("Validation Error")
                .status(400)
                .detail(detail)
                .instance(req.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ProblemDetail.builder()
                .type("about:blank")
                .title("Unauthorized")
                .status(401)
                .detail("Credenciais inválidas ou token expirado")
                .instance(req.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccess(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ProblemDetail.builder()
                .type("about:blank")
                .title("Forbidden")
                .status(403)
                .detail("Sem permissão para este recurso")
                .instance(req.getRequestURI())
                .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ProblemDetail.builder()
                .type("about:blank")
                .title("Internal Server Error")
                .status(500)
                .detail("Erro interno. Contate o suporte.")
                .instance(req.getRequestURI())
                .build()
        );
    }
}
