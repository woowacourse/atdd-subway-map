package wooteco.subway.exception.badRequest;

public class DuplicatedSectionException extends BadRequest {

    private static final String MESSAGE = "중복된 구간 정보입니다.";

    public DuplicatedSectionException() {
        super(MESSAGE);
    }
}
