package wooteco.subway.admin.controller.advice;

import org.springframework.http.HttpStatus;

public class ApiError {
    private Integer status;
    private String error;
    private String message;

    private ApiError() {
    }

    private ApiError(Integer status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ApiError(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), httpStatus.getReasonPhrase(), message);
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
