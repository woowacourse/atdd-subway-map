package wooteco.subway.dto;

public class ExceptionResponse {

    private String exceptionMessage;

    public ExceptionResponse() {
    }

    public ExceptionResponse(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
