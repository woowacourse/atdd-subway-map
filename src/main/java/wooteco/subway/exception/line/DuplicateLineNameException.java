package wooteco.subway.exception.line;

import wooteco.subway.exception.DuplicateNameException;

public class DuplicateLineNameException extends DuplicateNameException {
    private static final String MESSAGE = "같은 이름의 노선은 등록할 수 없습니다.";

    public DuplicateLineNameException() {
        super(MESSAGE);
    }
}
