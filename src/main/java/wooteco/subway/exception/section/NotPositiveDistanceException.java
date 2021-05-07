package wooteco.subway.exception.section;

public class NotPositiveDistanceException extends SectionException {

    private static final String MESSAGE = "자연수가 아닌 거리를 등록하셨습니다.";

    public NotPositiveDistanceException() {
        super(MESSAGE);
    }
}
