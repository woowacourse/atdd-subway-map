package wooteco.subway.service.exception;

abstract public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
