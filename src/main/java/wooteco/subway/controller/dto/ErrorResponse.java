package wooteco.subway.controller.dto;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

public class ErrorResponse {

    private static final String ERROR_MESSAGE_DELIMITER = ",";

    private final String message;

    private ErrorResponse(final String message) {
        this.message = message;
    }

    public static ErrorResponse from(final String message) {
        return new ErrorResponse(message);
    }

    public static ErrorResponse from(final Exception exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse from(final MethodArgumentNotValidException exception) {
        return new ErrorResponse(exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(ERROR_MESSAGE_DELIMITER)));
    }

    public String getMessage() {
        return message;
    }
}
