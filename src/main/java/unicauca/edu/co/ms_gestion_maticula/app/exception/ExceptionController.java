package unicauca.edu.co.ms_gestion_maticula.app.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
     @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return buildErrorResponse("Recurso no encontrado", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ConstraintViolationException ex) {
        return buildErrorResponse("Error de validación: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildErrorResponse("Parámetro inválido: " + ex.getName(), HttpStatus.BAD_REQUEST);
    }

     @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
      var errores = ex.getBindingResult().getFieldErrors().stream()
              .map(e -> e.getField() + ": " + e.getDefaultMessage())
              .toList();
      return new ResponseEntity<>(
              new ApiResponse("ERROR", "Solicitud inválida", errores, 400),
              HttpStatus.BAD_REQUEST);
  }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        System.out.println("Error interno del servidor: " + ex.getMessage());
        return buildErrorResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse> buildErrorResponse(String message, HttpStatus status) {
        ApiResponse response = new ApiResponse(
                "ERROR",
                message,
                null,
                status.value()
        );
        return new ResponseEntity<>(response, status);
    }

}
