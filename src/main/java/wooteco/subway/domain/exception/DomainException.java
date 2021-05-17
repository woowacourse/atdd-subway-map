package wooteco.subway.domain.exception;

abstract public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
