package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineNotFoundException extends SubwayException {
    public LineNotFoundException() {
        super("존재하지 않는 호선입니다!");
    }
}
