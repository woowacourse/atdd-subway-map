package wooteco.subway.exception.validation;

import wooteco.subway.exception.SubwayValidationException;

public class SectionDuplicateException extends SubwayValidationException {

    private static final String DEFAULT_MESSAGE = "이미 존재하는 구간입니다 : 상행역 - %s, 하행역 - %s";

    public SectionDuplicateException(String upStationName, String downStationName) {
        super(String.format(DEFAULT_MESSAGE, upStationName, downStationName));
    }
}
