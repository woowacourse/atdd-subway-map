package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

public class LineDuplicationException extends SubwayException {
    public LineDuplicationException() {
        super(HttpStatus.BAD_REQUEST, "이미 존재하는 노선입니다.");
    }
}
