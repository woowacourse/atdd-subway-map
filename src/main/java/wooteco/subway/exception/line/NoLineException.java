package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class NoLineException extends SubwayException {

    public NoLineException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 노선입니다.");
    }
}
