package wooteco.subway.exception.notfoundexception;

public class NotFoundSectionException extends NotFoundException {
    private static final String NOT_FOUND_SECTION_ERROR_MESSAGE = "해당 구간을 찾을 수 없습니다.";


    public NotFoundSectionException() {
        super(NOT_FOUND_SECTION_ERROR_MESSAGE);
    }
}
