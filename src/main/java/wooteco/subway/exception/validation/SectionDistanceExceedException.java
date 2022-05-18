package wooteco.subway.exception.validation;

import wooteco.subway.exception.SubwayValidationException;

public class SectionDistanceExceedException extends SubwayValidationException {

    private static final String DEFAULT_MESSAGE = "거리 초과로 인해 구간 추가에 실패하였습니다 : ";

    public SectionDistanceExceedException(int distance) {
        super(DEFAULT_MESSAGE + distance);
    }
}
