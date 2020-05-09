package wooteco.subway.admin.exception;

public class WrongIdException extends RuntimeException{
    private static final String ERROR_MESSAGE = "잘못된 id 입니다.";

    public WrongIdException() {
        super(ERROR_MESSAGE);
    }
}
