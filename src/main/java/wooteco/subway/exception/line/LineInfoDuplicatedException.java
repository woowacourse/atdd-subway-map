package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class LineInfoDuplicatedException extends SubwayException {

    private static final String MESSAGE = "이미 등록되어 있는 노선 정보입니다.";

    public LineInfoDuplicatedException() {
        super(MESSAGE);
    }
}
