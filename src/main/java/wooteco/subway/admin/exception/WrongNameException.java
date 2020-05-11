package wooteco.subway.admin.exception;

public class WrongNameException extends RuntimeException {
    private final static String ERROR_MESSAGE = "잘못된 이름 입니다.";

    public WrongNameException() {
        super(ERROR_MESSAGE);
    }
}
