package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class InvalidDeleteSectionException extends SubwayException {
    public InvalidDeleteSectionException() {
        super(HttpStatus.BAD_REQUEST, "삭제할 수 없는 구간입니다.");
    }
}
