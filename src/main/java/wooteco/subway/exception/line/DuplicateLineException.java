package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class DuplicateLineException extends SubwayException {

    public DuplicateLineException() {
        super("중복된 노선이 존재합니다.");
    }
}
