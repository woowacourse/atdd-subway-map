package wooteco.subway.exception;

public class NotFoundIdException extends RuntimeException {

    public NotFoundIdException() {
        super("존재하지 않는 ID입니다.");
    }
}
