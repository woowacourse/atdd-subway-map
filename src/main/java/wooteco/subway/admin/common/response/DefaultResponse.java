package wooteco.subway.admin.common.response;

public class DefaultResponse<T> {
    private T data;
    private String message;

    private DefaultResponse() {
    }

    public DefaultResponse(final T data, final String message) {
        this.data = data;
        this.message = message;
    }

    public static <Void> DefaultResponse<Void> error(String message) {
        return new DefaultResponse<>(null, message);
    }

    public static <T> DefaultResponse<T> of(T data) {
        return new DefaultResponse<>(data, null);
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
