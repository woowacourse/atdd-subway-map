package wooteco.subway.exception.notFound;

import wooteco.subway.exception.badRequest.BadRequest;

public class StationNotFoundException extends NotFoundException {

    private static final String MESSAGE = "존재하지 않는 역입니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
