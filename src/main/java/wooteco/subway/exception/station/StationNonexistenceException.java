package wooteco.subway.exception.station;

import wooteco.subway.exception.SubwayException;

public class StationNonexistenceException extends SubwayException {

    public StationNonexistenceException() {
        super("존재하지 않는 역입니다.");
    }
}


