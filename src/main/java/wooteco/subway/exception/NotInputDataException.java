package wooteco.subway.exception;

public class NotInputDataException extends RuntimeException {

    private static final String message = "[ERROR] 입력값이 존재하지 않습니다.";

    public NotInputDataException() {
        super(message);
    }
}
