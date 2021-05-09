package wooteco.subway.exception.station;

public class StationNameDuplicatedException extends RuntimeException {

    private static final String MESSAGE = "이미 등록되어 있는 역 이름입니다.";

    public StationNameDuplicatedException() {
        super(MESSAGE);
    }
}
