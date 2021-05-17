package wooteco.subway.exception.illegalexception;

public class IllegalSectionArgumentException extends IllegalException {

    private static final String CREATE_SECTION_ERROR_MESSAGE = "잘못된 구간 정보입니다.";

    public IllegalSectionArgumentException() {
        super(CREATE_SECTION_ERROR_MESSAGE);
    }
}
