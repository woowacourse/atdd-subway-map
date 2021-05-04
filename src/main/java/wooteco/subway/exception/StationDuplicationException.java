package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class StationDuplicationException extends SubwayException {
    public StationDuplicationException() {
        super(HttpStatus.BAD_REQUEST, "이미 존재하는 역입니다.");
    }
}
