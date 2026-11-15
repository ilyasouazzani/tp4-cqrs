package ma.ilyas;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler({CommandExecutionException.class, IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> businessError(Exception error) {
        Throwable cause = error;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        int status = cause instanceof IllegalArgumentException ? 400 : 409;
        return ResponseEntity.status(status).body(Map.of("error", cause.getMessage() == null ? "Commande refusée" : cause.getMessage()));
    }
}
