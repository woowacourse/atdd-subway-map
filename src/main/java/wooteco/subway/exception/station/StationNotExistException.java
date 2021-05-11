package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNotExistException extends StationException {
    public StationNotExistException(Long id) {
        super(HttpStatus.BAD_REQUEST, "id : " + id + "는 존재하지 않는 역입니다.");
    }
}
