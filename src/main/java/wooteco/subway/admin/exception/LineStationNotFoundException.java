package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LineStationNotFoundException extends RuntimeException {
    public LineStationNotFoundException(final Long id) {
        super(id + "번 역을 찾을 수 없습니다.");
    }
}
