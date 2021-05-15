package wooteco.subway.exception;

public class NotRemovableSectionException extends RuntimeException {

    private static final String message = "구간을 제거할 수 없습니다.";

    public NotRemovableSectionException() {
        super(message);
    }
}
