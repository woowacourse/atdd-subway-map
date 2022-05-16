package wooteco.subway.exception.notfound;

public class NotFoundSectionException extends NotFoundException {

    public NotFoundSectionException() {
        super("존재하지 않은 구간입니다.");
    }
}
