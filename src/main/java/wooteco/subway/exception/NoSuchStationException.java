package wooteco.subway.exception;

import java.util.NoSuchElementException;

public class NoSuchStationException extends NoSuchElementException {

    public NoSuchStationException(Long stationId) {
        super("id가 " + stationId + "인 역은 존재하지 않습니다.");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
