package wooteco.subway.common;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timeStamp;
    private String message;
    private String detail;

    private ErrorResponse() {
    }

    public ErrorResponse(String message, String detail) {
        this.timeStamp = LocalDateTime.now();
        this.message = message;
        this.detail = detail;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }
}
