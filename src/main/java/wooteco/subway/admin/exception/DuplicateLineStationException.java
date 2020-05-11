package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateLineStationException extends RuntimeException {
    public DuplicateLineStationException(final Long id) {
        super(id + "번 역은 이미 존재하므로 추가할 수 없습니다.");
    }
}
