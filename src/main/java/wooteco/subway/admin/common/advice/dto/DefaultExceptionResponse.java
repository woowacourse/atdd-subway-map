package wooteco.subway.admin.common.advice.dto;

public class DefaultExceptionResponse<T> {
    private T data;
    private String message;

    private DefaultExceptionResponse() {
    }

    public DefaultExceptionResponse(final T data, final String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
