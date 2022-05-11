package wooteco.subway.exception.notfound;

import wooteco.subway.exception.SubwayNotFoundException;

public class NotFoundLineException extends SubwayNotFoundException {

    private static final String NOT_FOUND_MESSAGE = "요청한 노선이 존재하지 않습니다 : ";

    public NotFoundLineException(Long id) {
        super(NOT_FOUND_MESSAGE + id);
    }
}
