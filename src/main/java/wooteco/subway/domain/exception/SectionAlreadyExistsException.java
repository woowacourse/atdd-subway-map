package wooteco.subway.domain.exception;

public class SectionAlreadyExistsException extends ExpectedException {

    private static final String MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";

    public SectionAlreadyExistsException() {
        super(MESSAGE);
    }
}
