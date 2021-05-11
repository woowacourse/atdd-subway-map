package wooteco.subway.common.exception.bad_request;

public class WrongLineInfoException extends BadRequestException {
    public WrongLineInfoException(String errorMessage) {
        super(errorMessage);
    }
}
