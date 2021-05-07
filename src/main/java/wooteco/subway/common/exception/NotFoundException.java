package wooteco.subway.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SubwayHttpException {

    public NotFoundException(String body) {
        super(HttpStatus.NOT_FOUND, body);
    }
}
