package wooteco.subway.domain.exception;

public class AlreadyRegisteredStationsException extends DomainException {
    public AlreadyRegisteredStationsException(String message) {
        super(message);
    }
}
