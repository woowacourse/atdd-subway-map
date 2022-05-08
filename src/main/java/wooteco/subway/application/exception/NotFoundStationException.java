package wooteco.subway.application.exception;

public class NotFoundStationException extends NotFoundException {

    public NotFoundStationException(long id) {
        super(String.format("%d와 동일한 ID의 지하철역을 찾을 수 없습니다.", id));
    }
}
