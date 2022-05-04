package wooteco.subway.exception.line;

import wooteco.subway.exception.CustomException;

public class LineNotFoundException extends CustomException {

    private static final String MESSAGE = "존재하지 않는 노선입니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }
}
