package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class IllegalIdException extends SubwayException {

    public IllegalIdException() {
        super(HttpStatus.BAD_REQUEST, "유효하지 않은 아이디입니다.");
    }
}
