package wooteco.subway.exception.notAddableSectionException;

public class OverDistanceException extends NotAddableSectionException {

    private static final String OVER_DISTANCE = "속한 구간의 길이를 넘는 구간은 추가할 수 없습니다.";

    public OverDistanceException() {
        super(OVER_DISTANCE);
    }
}
