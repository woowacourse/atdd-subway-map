package wooteco.subway.line.service;

public class NoSuchLineException extends LineException {
    private static final String MESSAGE = "존재하지 않는 노선입니다.";

    public NoSuchLineException() {
        super(MESSAGE);
    }
}
