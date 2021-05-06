package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineNotExistException extends LineException {
    public LineNotExistException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 노선입니다.");
    }
}
