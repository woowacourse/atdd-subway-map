package wooteco.subway.exception.section;

public class NotEnoughSectionException extends SectionException {

    private static final String MESSAGE = "최소 구간의 개수가 부족합니다.";

    public NotEnoughSectionException() {
        super(MESSAGE);
    }
}
