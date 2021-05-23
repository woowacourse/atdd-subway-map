package wooteco.subway.exception;

public class StationNotFoundException extends RuntimeException {

    private static final String message = "일치하는 역을 찾을 수 없습니다.";

    public StationNotFoundException() {
        super(message);
    }
}
