package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineDuplicateException extends LineException {
    public LineDuplicateException() {
        super(HttpStatus.BAD_REQUEST, "중복된 값이 존재합니다.");
    }
}
