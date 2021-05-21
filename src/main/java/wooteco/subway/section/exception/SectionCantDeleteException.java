package wooteco.subway.section.exception;

public class SectionCantDeleteException extends SectionException {
    private static final String MESSAGE = "구간이 1개인 경우에는 구간을 삭제할 수 없습니다.";

    public SectionCantDeleteException() {
        super(MESSAGE);
    }
}
