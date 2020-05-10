package wooteco.subway.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStationInsertionException extends RuntimeException {
    public InvalidStationInsertionException(Long preStationId) {
        super(preStationId + "번에 해당하는 역이 존재하지 않습니다.");
    }
}
