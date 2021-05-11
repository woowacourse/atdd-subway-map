package wooteco.subway.exception;

public class NotFoundStationException extends NotFoundException {

    public NotFoundStationException() {
        super("해당 역이 존재하지 않습니다.");
    }
}
