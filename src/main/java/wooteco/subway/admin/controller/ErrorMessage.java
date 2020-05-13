package wooteco.subway.admin.controller;

public class ErrorMessage<T extends Throwable> {
    private String message;
    private T data;

    public ErrorMessage(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
