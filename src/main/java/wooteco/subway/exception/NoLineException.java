package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NoLineException extends SubwayException {

    public NoLineException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 노선입니다.");
    }
}
