package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class StationDuplicationException extends SubwayException {
    public StationDuplicationException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 역입니다.");
    }
}
