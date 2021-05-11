package wooteco.subway.exception.badRequest;

public class InvalidSectionException extends BadRequest {

    private static final String MESSAGE = "잘못된 역 정보입니다.";

    public InvalidSectionException() {
        super(MESSAGE);
    }
}
