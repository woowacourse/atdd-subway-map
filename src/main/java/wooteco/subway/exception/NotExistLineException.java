package wooteco.subway.exception;

public class NotExistLineException extends NotExistItemException{

    private static final String MESSAGE = "라인이 존재하지 않습니다.";

    public NotExistLineException() {
        super(MESSAGE);
    }

}
