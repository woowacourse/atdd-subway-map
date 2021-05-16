package wooteco.subway.exception;

public class InvalidSectionsException extends RuntimeException {

    private static final String MESSAGE = "연결되지 않는 구간 목록 입니다.";

    public InvalidSectionsException() {
        super(MESSAGE);
    }

}
