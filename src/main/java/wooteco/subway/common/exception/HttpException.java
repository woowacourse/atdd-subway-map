package wooteco.subway.common.exception;

import org.springframework.http.HttpStatus;

public interface HttpException {

    HttpStatus httpStatus();

    Object body();
}
