package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;

public class ErrorMessage {
    private HttpStatus error;
    private String message;

    public ErrorMessage(HttpStatus error, String message) {
        this.error = error;
        this.message = message;
    }

    public HttpStatus getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
