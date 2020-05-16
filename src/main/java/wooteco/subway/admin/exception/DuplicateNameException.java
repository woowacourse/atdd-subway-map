package wooteco.subway.admin.exception;

public class DuplicateNameException extends IllegalArgumentException {

    private static final String FORMATTED_ERROR_MESSAGE = "%s : 이미 존재하는 이름입니다.";

    public DuplicateNameException(String name) {
        super(String.format(FORMATTED_ERROR_MESSAGE, name));
    }
}
