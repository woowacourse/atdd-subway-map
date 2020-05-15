package wooteco.subway.admin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateLineException extends RuntimeException {
    public DuplicateLineException(String name) {
        super(name + "은/는 이미 사용되고 있는 노선 이름입니다.");
    }
}
