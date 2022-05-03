package wooteco.subway.exception;

public class NoStationFoundException extends RuntimeException {

    private static final String NOT_FOUND_MESSAGE = "요청한 지하철 역이 존재하지 않습니다.";

    public NoStationFoundException() {
        super(NOT_FOUND_MESSAGE);
    }
}
