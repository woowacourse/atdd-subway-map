package wooteco.subway.exception;

import org.springframework.http.HttpStatus;

public class LineDuplicationException extends SubwayException {
    public LineDuplicationException() {
        super(HttpStatus.BAD_REQUEST, "이미 존재하는 노선색깔입니다.");
    }
}
