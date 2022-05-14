package wooteco.subway.dto.response;

public class ExceptionResponse {

    private final Class<? extends Exception> exception;
    private final String message;

    public ExceptionResponse(Class<? extends Exception> exception, String message) {
        this.exception = exception;
        this.message = message;
    }

    private ExceptionResponse() {
        this(null, null);
    }

    public static ExceptionResponse of(Exception exception) {
        return new ExceptionResponse(exception.getClass(), exception.getMessage());
    }

    public Class<? extends Exception> getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
}
