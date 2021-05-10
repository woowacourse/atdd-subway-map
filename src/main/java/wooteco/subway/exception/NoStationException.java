package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NoStationException extends SubwayException {

    public NoStationException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 역입니다.");
    }
}


