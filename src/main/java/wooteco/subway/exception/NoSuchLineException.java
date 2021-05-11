package wooteco.subway.exception;

public class NoSuchLineException extends NoSuchException {

    private static final String MESSAGE = "노선이 존재하지 않습니다.";

    public NoSuchLineException() {
        super(MESSAGE);
    }
}
