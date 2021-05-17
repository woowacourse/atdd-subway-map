package wooteco.subway.exception.illegalexception;

public class IllegalSectionStatusException extends IllegalException {

    private static final String SECTION_STATUS_ERROR_MESSAGE = "잘못된 구간 정보입니다.";

    public IllegalSectionStatusException() {
        super(SECTION_STATUS_ERROR_MESSAGE);
    }
}
