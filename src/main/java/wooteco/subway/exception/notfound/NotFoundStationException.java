package wooteco.subway.exception.notfound;

import wooteco.subway.exception.SubwayNotFoundException;

public class NotFoundStationException extends SubwayNotFoundException {

    private static final String NOT_FOUND_MESSAGE = "요청한 지하철역이 존재하지 않습니다";

    public NotFoundStationException() {
        super(NOT_FOUND_MESSAGE);
    }

    public NotFoundStationException(Long id) {
        super(NOT_FOUND_MESSAGE + " : " + id);
    }
}
