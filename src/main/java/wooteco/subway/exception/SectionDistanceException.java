package wooteco.subway.exception;

public class SectionDistanceException extends RuntimeException {

    private static final String MESSAGE = "구간 길이가 잘못되었습니다.";

    public SectionDistanceException() {
        super(MESSAGE);
    }

}
