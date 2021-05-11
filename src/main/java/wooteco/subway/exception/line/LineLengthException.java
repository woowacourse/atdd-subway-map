package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineLengthException extends SubwayException {
    public LineLengthException() {
        super("2자 이상의 이름을 입력해주세요.");
    }
}
