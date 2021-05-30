package wooteco.subway.exception.duplicateException;

public class StationDuplicationException extends RuntimeException {

    private static final String STATION_DUPLICATE = "이미 등록된 역입니다.";

    public StationDuplicationException() {
        super(STATION_DUPLICATE);
    }
}
