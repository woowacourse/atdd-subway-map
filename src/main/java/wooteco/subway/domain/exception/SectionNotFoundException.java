package wooteco.subway.domain.exception;

public class SectionNotFoundException extends ExpectedException {

    private static final String MESSAGE = "해당 지하철역으로 구성된 구간이 존재하지 않습니다.";

    public SectionNotFoundException() {
        super(MESSAGE);
    }
}
