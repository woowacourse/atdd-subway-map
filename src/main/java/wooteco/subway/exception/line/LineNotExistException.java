package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineNotExistException extends LineException {
    public LineNotExistException(Long id) {
        super(HttpStatus.BAD_REQUEST, "id : " + id + "는 존재하지 않는 노선입니다.");
    }
}
