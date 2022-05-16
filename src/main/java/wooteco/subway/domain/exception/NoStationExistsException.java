package wooteco.subway.domain.exception;

public class NoStationExistsException extends ExpectedException {

    private static final String MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.";

    public NoStationExistsException() {
        super(MESSAGE);
    }
}
