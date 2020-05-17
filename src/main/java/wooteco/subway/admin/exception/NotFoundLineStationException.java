package wooteco.subway.admin.exception;

public class NotFoundLineStationException extends RuntimeException {
    private static final String NOT_FOUND_LINE_STATION_EXCEPTION_MESSAGE = "존재하지 않는 구간";

    public NotFoundLineStationException() {
        super(NOT_FOUND_LINE_STATION_EXCEPTION_MESSAGE);
    }
}
