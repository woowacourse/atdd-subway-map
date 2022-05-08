package wooteco.subway.exception;

public class StationNotFoundException extends NotFoundException {

    public StationNotFoundException() {
        super("존재하지 않는 지하철역 입니다.");
    }
}
