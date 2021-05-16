package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationDeleteException extends StationException {
    public StationDeleteException(Long stationId) {
        super(HttpStatus.BAD_REQUEST, stationId + " 역이 구간에 존재해 삭제할 수 없습니다.");
    }
}
