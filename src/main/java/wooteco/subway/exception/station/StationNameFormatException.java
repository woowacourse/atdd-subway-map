package wooteco.subway.exception.station;

import org.springframework.http.HttpStatus;

public class StationNameFormatException extends StationException {
    public StationNameFormatException(String name) {
        super(HttpStatus.BAD_REQUEST, "\"" + name + "\"은 역의 이름 패턴과 맞지 않습니다.");
    }
}
