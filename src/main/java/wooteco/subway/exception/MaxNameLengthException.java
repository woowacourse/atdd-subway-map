package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class MaxNameLengthException extends SubwayException {

    public MaxNameLengthException() {
        super(HttpStatus.BAD_REQUEST, "[ERROR] 이름이 최대 길이를 초과했습니다.");
    }
}
