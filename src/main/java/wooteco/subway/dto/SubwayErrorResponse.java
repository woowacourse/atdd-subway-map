package wooteco.subway.dto;

import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class SubwayErrorResponse {

    private static final String MESSAGE_JOINING_DELIMITER = ",";

    private String message;

    private SubwayErrorResponse() {
    }

    private SubwayErrorResponse(final String message) {
        this.message = message;
    }

    public static SubwayErrorResponse from(RuntimeException exception) {
        return new SubwayErrorResponse(exception.getMessage());
    }

    public static SubwayErrorResponse from(MethodArgumentNotValidException exception) {
        return new SubwayErrorResponse(exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(MESSAGE_JOINING_DELIMITER)));
    }

    public String getMessage() {
        return message;
    }
}
