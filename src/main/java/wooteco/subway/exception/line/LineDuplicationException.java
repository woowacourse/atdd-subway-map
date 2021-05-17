package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineDuplicationException extends SubwayException {
    public LineDuplicationException() {
        super("이미 존재하는 노선색깔입니다.");
    }
}
