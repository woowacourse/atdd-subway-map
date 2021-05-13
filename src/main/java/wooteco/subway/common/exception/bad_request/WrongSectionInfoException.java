package wooteco.subway.common.exception.bad_request;

public class WrongSectionInfoException extends BadRequestException {
    public WrongSectionInfoException(String errorMessage) {
        super(errorMessage);
    }
}
