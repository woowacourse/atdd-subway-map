package wooteco.subway.line.repository;

public class NoSuchLineException extends RuntimeException {
    private static final String MESSAGE = "존재하지 않는 노선입니다.";

    public NoSuchLineException() {
        super(MESSAGE);
    }
}
