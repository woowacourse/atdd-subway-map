package wooteco.subway.exception;

public class IllegalSectionStatusException extends SubwayException {

    private static final String SECTION_STATUS_ERROR_MESSAGE = "잘못된 구간 정보입니다.";

    public IllegalSectionStatusException() {
        super(SECTION_STATUS_ERROR_MESSAGE);
    }
}
