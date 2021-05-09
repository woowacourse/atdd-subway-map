package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineColorDuplicateException extends LineException {
    public LineColorDuplicateException() {
        super(HttpStatus.BAD_REQUEST, "중복된 색을 가진 노선선 존재합니다.");
    }
}
