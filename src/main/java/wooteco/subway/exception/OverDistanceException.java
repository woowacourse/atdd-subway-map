package wooteco.subway.exception;

public class OverDistanceException extends NotAddableSectionException {

    private static final String OVER_DISTANCE_EXCEPTION = "속한 구간의 길이를 넘는 구간은 추가할 수 없습니다.";

    public OverDistanceException() {
        super(OVER_DISTANCE_EXCEPTION);
    }
}
