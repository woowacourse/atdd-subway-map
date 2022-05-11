package wooteco.subway.exception.section;

import wooteco.subway.exception.InvalidSubwayResourceException;

public class NonExistenceStationsSectionException extends InvalidSubwayResourceException {
    private static final String MESSAGE = "상행선과 하행선이 둘다 현재 노선에 존재하지 않습니다.";

    private static final NonExistenceStationsSectionException INSTANCE = new NonExistenceStationsSectionException();

    public static NonExistenceStationsSectionException getInstance() {
        return INSTANCE;
    }

    private NonExistenceStationsSectionException() {
        super(MESSAGE);
    }
}
