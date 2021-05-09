package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationDuplicateException extends StationException {
    public StationDuplicateException(String name) {
        super(HttpStatus.BAD_REQUEST, name + "역이 이미 존재합니다.");
    }
}
