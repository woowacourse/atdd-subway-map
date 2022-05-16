package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class DuplicateNameException extends SubwayException {
    private static final String MESSAGE = "중복된 이름입니다.";

    public DuplicateNameException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }

    public DuplicateNameException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
