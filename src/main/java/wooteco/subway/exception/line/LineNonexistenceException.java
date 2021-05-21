package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineNonexistenceException extends SubwayException {

    public LineNonexistenceException() {
        super("존재하지 않는 노선입니다.");
    }
}
