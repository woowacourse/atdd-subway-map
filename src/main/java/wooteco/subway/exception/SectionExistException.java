package wooteco.subway.exception;

public class SectionExistException extends NotAddableSectionException {

    private static final String EXIST_SECTION_MESSAGE = "이미 등록된 구간입니다.";

    public SectionExistException() {
        super(EXIST_SECTION_MESSAGE);
    }
}
