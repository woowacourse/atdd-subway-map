package wooteco.subway.exception.notfoundexception;

import wooteco.subway.exception.SubwayException;

public class NotFoundException extends SubwayException {
    public NotFoundException(String message) {
        super(message);
    }
}
