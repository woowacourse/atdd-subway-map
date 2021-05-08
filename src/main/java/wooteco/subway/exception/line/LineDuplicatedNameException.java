package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineDuplicatedNameException extends SubwayException {
    public LineDuplicatedNameException() {
        super("중복된 이름의 노선이 존재합니다");
    }
}
