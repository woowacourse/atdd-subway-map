package wooteco.subway.common.exception.not_found;

public class NotFoundStationInfoException extends NotFoundException {
    public NotFoundStationInfoException(String errorMessage) {
        super(errorMessage);
    }
}
