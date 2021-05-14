package wooteco.subway.exception.badRequest;

public class InvalidDistanceException extends BadRequest {

    private static final String MESSAGE = "잘못된 거리 정보입니다.";

    public InvalidDistanceException() {
        super(MESSAGE);
    }
}
