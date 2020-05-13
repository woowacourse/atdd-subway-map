package wooteco.subway.admin.exception;

public class NotFoundLineIdException extends RuntimeException {

    public NotFoundLineIdException() {
        super("해당 ID를 찾을 수 없습니다.");
    }
}
