package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateStationException extends RuntimeException {
    public DuplicateStationException(String name) {
        super(String.format("역 이름이 중복됩니다. (%s)", name));
    }
}
