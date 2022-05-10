package wooteco.subway.exception.dto;

import org.springframework.http.HttpStatus;

public class ErrorResult {

    private HttpStatus status;
    private String message;

    public ErrorResult(HttpStatus status) {
        this.status = status;
        message = status.getReasonPhrase();
    }

    public ErrorResult(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }
}
