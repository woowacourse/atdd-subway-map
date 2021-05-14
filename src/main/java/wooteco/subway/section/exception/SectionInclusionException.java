package wooteco.subway.section.exception;

public class SectionInclusionException extends SectionException {
    private static final String MESSAGE = "구간의 양 역이 노선에 둘 다 존재해서는 안되고, 둘 다 존재하지 않아서도 안됩니다.";

    public SectionInclusionException() {
        super(MESSAGE);
    }
}
