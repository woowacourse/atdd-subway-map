package wooteco.subway.common.exception.bad_request;

public class WrongStationInfoException extends BadRequestException {
    public WrongStationInfoException(String errorMessage) {
        super(errorMessage);
    }
}
