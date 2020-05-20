package wooteco.subway.admin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateLineException extends RuntimeException {
    public DuplicateLineException(String name) {
        super(name + "은 이미 존재하는 노선 입니다.");
    }
}
