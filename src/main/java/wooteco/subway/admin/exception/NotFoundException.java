package wooteco.subway.admin.exception;

public class NotFoundException extends IllegalArgumentException {

    private static final String FORMATTED_ERROR_MESSAGE = "%d : 존재하지 않는 id입니다";

    public NotFoundException(Long id) {
        super(String.format(FORMATTED_ERROR_MESSAGE, id));
    }
}
