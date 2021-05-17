package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class NoLineException extends SubwayException {

    public NoLineException() {
        super("존재하지 않는 노선입니다.");
    }
}