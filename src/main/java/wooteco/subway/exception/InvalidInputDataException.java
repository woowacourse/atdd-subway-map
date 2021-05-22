package wooteco.subway.exception;

public class InvalidInputDataException extends RuntimeException {

    private static final String MESSAGE = "[ERROR] 입력값이 존재하지 않습니다.";

    public InvalidInputDataException() {
        super(MESSAGE);
    }
}
