package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayNotFoundException;

public class LineNotFoundException extends SubwayNotFoundException {

    private static final String MESSAGE = "해당하는 라인이 존재하지 않습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }
}
