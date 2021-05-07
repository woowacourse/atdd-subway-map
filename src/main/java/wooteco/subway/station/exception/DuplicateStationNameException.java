package wooteco.subway.station.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateStationNameException extends DuplicateKeyException {
    public DuplicateStationNameException(String msg) {
        super(msg);
    }
}
