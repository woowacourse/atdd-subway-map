package wooteco.subway.web.exception;

import org.springframework.http.HttpStatus;

public interface HttpException {

    HttpStatus httpStatus();

    Object body();
}
