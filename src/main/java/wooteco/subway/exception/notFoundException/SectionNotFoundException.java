package wooteco.subway.exception.notFoundException;

public class SectionNotFoundException extends NotFoundException {

    private static final String SECTION_NOT_FOUND = "일치하는 구간을 찾을 수 없습니다.";

    public SectionNotFoundException() {
        super(SECTION_NOT_FOUND);
    }
}
