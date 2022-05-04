package wooteco.subway.exception.line;

import wooteco.subway.exception.CustomException;

public class DuplicatedLineException extends CustomException {

    private static final String MESSAGE = "노선 이름 혹은 노선 색이 이미 존재합니다.";

    public DuplicatedLineException() {
        super(MESSAGE);
    }
}
