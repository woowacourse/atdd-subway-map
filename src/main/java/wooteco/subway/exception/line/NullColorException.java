package wooteco.subway.exception.line;

import wooteco.subway.exception.NullException;

public class NullColorException extends NullException {

    public NullColorException() {
        super("색상 값이 입력되지 않았습니다.");
    }
}

