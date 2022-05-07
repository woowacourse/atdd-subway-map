package wooteco.subway.exception.line;

import wooteco.subway.exception.IllegalInputException;

public class DuplicateLineException extends IllegalInputException {

    private static final String MESSAGE = "노선 정보가 중복됩니다.";

    public DuplicateLineException() {
        super(MESSAGE);
    }
}
