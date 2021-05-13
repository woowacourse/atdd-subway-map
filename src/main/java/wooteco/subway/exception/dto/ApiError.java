package wooteco.subway.exception.dto;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class ApiError {

    private LocalDateTime localDateTime;
    private Integer status;
    private String error;
    private String message;

    public ApiError(HttpStatus httpStatus, String message) {
        this.localDateTime = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
