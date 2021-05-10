package wooteco.subway.section.exception;

public class SectionHasSameStationsException extends IllegalArgumentException {
    public SectionHasSameStationsException(String msg) {
        super(msg);
    }
}
