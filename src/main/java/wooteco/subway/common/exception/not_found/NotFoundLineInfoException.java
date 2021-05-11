package wooteco.subway.common.exception.not_found;

public class NotFoundLineInfoException extends NotFoundException {
    public NotFoundLineInfoException(String errorMessage) {
        super(errorMessage);
    }
}
