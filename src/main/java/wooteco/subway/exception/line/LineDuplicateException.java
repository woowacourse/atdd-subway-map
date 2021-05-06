package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineDuplicateException extends LineException {
    public LineDuplicateException() {
        super(HttpStatus.BAD_REQUEST, "해당 이름의 노선이 이미 존재합니다.");
    }
}
