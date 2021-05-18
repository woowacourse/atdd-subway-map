package wooteco.subway.section.exception;

public class SectionException extends RuntimeException {
    private SectionError sectionError;

    public SectionException(SectionError sectionError) {
        super(sectionError.getMessage());
        this.sectionError = sectionError;
    }

    public int statusCode() {
        return sectionError.getStatusCode();
    }
}
