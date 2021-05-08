package wooteco.subway.line.service;

public class DuplicateLineNameException extends LineException {
    private static final String MESSAGE = "중복된 노선 이름입니다.";

    public DuplicateLineNameException() {
        super(MESSAGE);
    }
}
