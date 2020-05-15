package wooteco.subway.admin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StationNotFoundOnSuchLine extends RuntimeException {
    public StationNotFoundOnSuchLine() {
        super("이 노선에 등록되어 있지 않습니다");
    }
}
