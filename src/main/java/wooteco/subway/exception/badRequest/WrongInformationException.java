package wooteco.subway.exception.badRequest;

public class WrongInformationException extends BadRequest{

    private static final String MESSAGE = "잘못된 정보를 입력했습니다.";

    public WrongInformationException() {
        super(MESSAGE);
    }
}
