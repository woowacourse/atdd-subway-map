package wooteco.subway.exception.section;

public class InsufficientSectionInformationException extends SectionException {

    private static final String MESSAGE = "필수값이 잘못 되었습니다.";

    public InsufficientSectionInformationException() {
        super(MESSAGE);
    }
}
