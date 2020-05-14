package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateLineException extends RuntimeException {
    public DuplicateLineException(String name) {
        super(String.format("노선명이 중복됩니다. (%s)", name));
    }
}
