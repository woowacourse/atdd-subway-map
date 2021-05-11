package wooteco.subway.exception.notfoundexception;

public class NotFoundStationException extends NotFoundException {

    private static final String NOT_FOUND_STATION_ERROR_MESSAGE = "해당 역을 찾을 수 없습니다.";

    public NotFoundStationException() {
        super(NOT_FOUND_STATION_ERROR_MESSAGE);
    }
}
