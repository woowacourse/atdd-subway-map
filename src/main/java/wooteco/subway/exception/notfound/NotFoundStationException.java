package wooteco.subway.exception.notfound;

public class NotFoundStationException extends NotFoundException {

    public NotFoundStationException() {
        super("존재하지 않은 역입니다.");
    }
}
