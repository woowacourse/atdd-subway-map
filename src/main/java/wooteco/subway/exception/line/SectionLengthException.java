package wooteco.subway.exception.line;

public class SectionLengthException extends SubwayLineException {
    private static final String MESSAGE = "기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.";

    public SectionLengthException() {
        super(MESSAGE);
    }
}
