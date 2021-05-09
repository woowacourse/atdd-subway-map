package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class EmptyInputException extends SubwayException {

    public EmptyInputException() {
        super(HttpStatus.BAD_REQUEST, "빈 값이 입력되었습니다.");
    }
}
