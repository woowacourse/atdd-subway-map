package wooteco.subway.exception.notfound;

public class NotFoundLineException extends NotFoundException {

    public NotFoundLineException() {
        super("존재하지 않은 노션입니다.");
    }
}
