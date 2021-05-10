package wooteco.subway.exception;

public class NoSuchLineException extends IllegalArgumentException {

    private static final String MESSAGE = "역이 존재하지 않습니다.";

    public NoSuchLineException() {
        super(MESSAGE);
    }
}
