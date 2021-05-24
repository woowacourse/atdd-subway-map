package wooteco.subway.exception.line;

import wooteco.subway.exception.InvalidRequestException;

public class LineDuplicatedInformationException extends InvalidRequestException {
    private static final String MESSAGE = "중복되는 라인 정보가 존재합니다.";

    public LineDuplicatedInformationException() {
        super(MESSAGE);
    }
}
