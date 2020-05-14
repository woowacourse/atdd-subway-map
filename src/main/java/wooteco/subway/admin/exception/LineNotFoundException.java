package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(Long id) {
        super(id + "번 호선을 찾을 수 없습니다.");
    }
}
