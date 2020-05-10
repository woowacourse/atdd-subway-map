package wooteco.subway.admin.exception;

public class NotFoundStationException extends RuntimeException {
    private static final String NOT_FOUND_STATION_EXCEPTION_MESSAGE = "존재하지 않는 역";

    public NotFoundStationException() {
        super(NOT_FOUND_STATION_EXCEPTION_MESSAGE);
    }
}
