package wooteco.subway.admin.dto;

public class ExceptionResponse {

    private String message;

    public ExceptionResponse() {
    }

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public static ExceptionResponse of(Exception e) {
        System.out.println(e.getMessage());
        return new ExceptionResponse(e.getMessage());
    }

    public String getMessage() {
        return message;
    }
}
