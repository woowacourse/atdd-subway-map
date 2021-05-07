package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class InvalidAddSectionException extends SubwayException {
    public InvalidAddSectionException() {
        super(HttpStatus.BAD_REQUEST, "추가할 수 없는 구간입니다.");
    }
}
