package wooteco.subway.admin.exception;

public class NotExistIdException extends RuntimeException {
    public NotExistIdException(final Long id) {
        super(id + "인 id값이 존재하지 않습니다!");
    }
}
