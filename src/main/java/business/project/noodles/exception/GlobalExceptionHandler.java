package business.project.noodles.exception;

import business.project.noodles.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException appException){
        log.error("Exception: ", appException);
        return ResponseEntity.status(appException.getErrorCode().getHttp_status())
                .body(ApiResponse.builder()
                        .code(appException.getErrorCode().getCode())
                        .message(appException.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException){
        log.error("Exception: ", methodArgumentNotValidException);

        // get enum key
        String enumKey = methodArgumentNotValidException.getFieldError().getDefaultMessage(); // vd: TOKEN_INVALID
        ErrorCode errorCode = ErrorCode.INVALID_KEY; // giá trị mặc định, vd khi ghi sai (103)

        Map<String, Object> attribute = null;

        // get message of key
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            // get object chứa attribute cần thiết
            var constraintViolation = methodArgumentNotValidException
                    .getBindingResult()
                    .getAllErrors()
                    .get(0)
                    .unwrap(ConstraintViolation.class);

            attribute = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info("Attribute: {}", attribute.toString());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .message(
                                Objects.nonNull(attribute)
                                        ? mapAttribute(errorCode.getMessage(), attribute)
                                        : errorCode.getMessage())
                        .code(errorCode.getCode())
                        .build());
    }

    private String mapAttribute(String messageTemplate, Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) return messageTemplate;
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            messageTemplate = messageTemplate.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return messageTemplate;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<ApiResponse> handlingIllegalArgument(IllegalArgumentException argumentException) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .message(argumentException.getMessage())
                        .build());
    }
}
