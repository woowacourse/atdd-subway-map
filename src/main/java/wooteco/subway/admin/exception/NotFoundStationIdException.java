package wooteco.subway.admin.exception;

public class NotFoundStationIdException extends RuntimeException {

    public NotFoundStationIdException() {
        super("해당 ID를 찾을 수 없습니다.");
    }
}
