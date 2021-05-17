package wooteco.subway.web.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SubwayHttpException {

    public static final String ERROR_MESSAGE_FORMAT = "존재하지 않는 %s 입니다.";

    public NotFoundException(String resourceName) {
        super(HttpStatus.NOT_FOUND,
                String.format(ERROR_MESSAGE_FORMAT, resourceName));
    }
}
