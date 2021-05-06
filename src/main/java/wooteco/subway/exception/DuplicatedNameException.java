package wooteco.subway.exception;

public class DuplicatedNameException extends RuntimeException {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름입니다.";

    public DuplicatedNameException() {
        super(DUPLICATED_NAME_ERROR_MESSAGE);
    }

    public DuplicatedNameException(Throwable cause) {
        this(DUPLICATED_NAME_ERROR_MESSAGE, cause);
    }

    public DuplicatedNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
