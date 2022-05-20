package wooteco.subway.exception.validation;

import wooteco.subway.exception.SubwayValidationException;

public class SectionNotSuitableException extends SubwayValidationException {

    private static final String DEFAULT_MESSAGE = "일치하는 역이 없어 구간 추가에 실패하였습니다 : 상행역 - %s, 하행역 - %s";

    public SectionNotSuitableException(String upStationName, String downStationName) {
        super(String.format(DEFAULT_MESSAGE, upStationName, downStationName));
    }
}
