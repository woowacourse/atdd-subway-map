package wooteco.subway.exception.line;

import wooteco.subway.exception.SubwayException;

public class DuplicatedLineInformationException extends SubwayException {
    private static final String MESSAGE = "중복되는 라인 정보가 존재합니다.";

    public DuplicatedLineInformationException() {
        super(MESSAGE);
    }
}
