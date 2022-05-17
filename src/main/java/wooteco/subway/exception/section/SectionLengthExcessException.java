package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidInputException;

public class SectionLengthExcessException extends InvalidInputException {

    private static final String MESSAGE = "등록하려는 구간의 길이가 기존 역 사이의 길이보다 크거나 같습니다.";

    public SectionLengthExcessException() {
        super(MESSAGE);
    }
}
