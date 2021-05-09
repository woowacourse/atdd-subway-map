package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineDuplicateException extends LineException {
    public LineDuplicateException(String input) {
        super(HttpStatus.BAD_REQUEST, "중복된 " + input + " 입니다.");
    }
}
