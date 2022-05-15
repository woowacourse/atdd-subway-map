package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class LineColorLengthException extends SubwayException {

    public LineColorLengthException() {
        super(HttpStatus.BAD_REQUEST, "[ERROR] 노선 색은 20자 이하여야 합니다.");
    }
}
