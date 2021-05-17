package wooteco.subway.exception;

import wooteco.subway.web.exception.SubwayHttpException;

public class DuplicatedNameException extends SubwayHttpException {

    public static final String ERROR_MESSAGE_FORMAT = "중복된 %s 이름입니다.";

    public DuplicatedNameException(String resourceName) {
        super(String.format(ERROR_MESSAGE_FORMAT, resourceName));
    }
}
