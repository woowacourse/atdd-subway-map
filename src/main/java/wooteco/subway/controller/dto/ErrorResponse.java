package wooteco.subway.controller.dto;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

public class ErrorResponse {

    private static final String ERROR_MESSAGE_DELIMITER = ",";

    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
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
