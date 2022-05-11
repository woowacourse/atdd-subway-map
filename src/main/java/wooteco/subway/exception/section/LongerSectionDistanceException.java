package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class LongerSectionDistanceException extends InvalidSubwayResourceException {
    private static final String MESSAGE = "기존의 구간보다 추가하려는 구간의 거리가 깁니다.";

    private static final LongerSectionDistanceException INSTANCE = new LongerSectionDistanceException();

    public static LongerSectionDistanceException getInstance() {
        return INSTANCE;
    }

    private LongerSectionDistanceException() {
        super(MESSAGE);
    }
}
