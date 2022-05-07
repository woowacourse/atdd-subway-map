package wooteco.subway.exception.line;

import wooteco.subway.exception.NoSuchRecordException;

public class NoSuchLineException extends NoSuchRecordException {

    private static final String MESSAGE = "존재하지 않는 노선입니다.";

    public NoSuchLineException() {
        super(MESSAGE);
    }
}
