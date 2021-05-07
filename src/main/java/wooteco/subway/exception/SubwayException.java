package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {
    private final ExceptionInformation information;

    public SubwayException(ExceptionInformation information) {
        super(information.getMessage());
        this.information = information;
    }

    public HttpStatus getHttpStatus() {
        return this.information.getHttpStatus();
    }

    public String getMessage() {
        return this.information.getMessage();
    }
}
