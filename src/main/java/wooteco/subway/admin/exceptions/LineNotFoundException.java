package wooteco.subway.admin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException() {
        super("찾는 노선이 존재하지 않습니다.");
    }
}
