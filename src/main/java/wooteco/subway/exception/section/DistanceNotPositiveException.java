package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidRequestException;

public class DistanceNotPositiveException extends InvalidRequestException {

    private static final String MESSAGE = "자연수가 아닌 거리를 등록하셨습니다.";

    public DistanceNotPositiveException() {
        super(MESSAGE);
    }
}
