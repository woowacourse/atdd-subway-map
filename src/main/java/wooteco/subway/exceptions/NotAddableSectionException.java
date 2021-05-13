package wooteco.subway.exceptions;

public class NotAddableSectionException extends RuntimeException {

    private static final String message = "생성할 수 없는 구간입니다.";

    public NotAddableSectionException() {
        super(message);
    }
}
