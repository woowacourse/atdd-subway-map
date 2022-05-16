package wooteco.subway.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(ExceptionType type) {
        super(type.getMessage());
    }
}
