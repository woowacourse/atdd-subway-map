package wooteco.subway.exception;

public class NotRemovableSectionException extends NotRemovableException {

    private static final String NOT_REMOVABLE_SECTION = "구간을 제거할 수 없습니다.";

    public NotRemovableSectionException() {
        super(NOT_REMOVABLE_SECTION);
    }
}
