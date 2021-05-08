package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class NullInputException extends SubwayException {

    public NullInputException() {
        super(HttpStatus.BAD_REQUEST, "null이 입력되었습니다.");
    }
}
