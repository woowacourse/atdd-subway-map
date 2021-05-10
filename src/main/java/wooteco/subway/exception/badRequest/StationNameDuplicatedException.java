package wooteco.subway.exception.badRequest;

public class StationNameDuplicatedException extends BadRequest {

    private static final String MESSAGE = "중복된 역 이름입니다.";

    public StationNameDuplicatedException() {
        super(MESSAGE);
    }
}
