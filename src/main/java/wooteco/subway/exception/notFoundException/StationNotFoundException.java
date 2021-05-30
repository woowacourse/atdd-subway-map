package wooteco.subway.exception.notFoundException;

public class StationNotFoundException extends NotFoundException {

    private static final String STATION_NOT_FOUND = "일치하는 역을 찾을 수 없습니다.";

    public StationNotFoundException() {
        super(STATION_NOT_FOUND);
    }
}
