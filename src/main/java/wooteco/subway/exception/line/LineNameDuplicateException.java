package wooteco.subway.exception.line;

import org.springframework.http.HttpStatus;

public class LineNameDuplicateException extends LineException {
    public LineNameDuplicateException() {
        super(HttpStatus.BAD_REQUEST, "중복된 노선 명이 존재합니다.");
    }
}
