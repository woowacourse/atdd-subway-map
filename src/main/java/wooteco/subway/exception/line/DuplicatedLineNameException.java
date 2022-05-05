package wooteco.subway.exception.line;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class DuplicatedLineNameException extends InvalidSubwayResourceException {

    private static final String MESSAGE = "노선 이름 혹은 노선 색이 이미 존재합니다.";

    public DuplicatedLineNameException() {
        super(MESSAGE);
    }
}
