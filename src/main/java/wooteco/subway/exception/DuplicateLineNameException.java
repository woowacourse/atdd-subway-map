package wooteco.subway.exception;

public class DuplicateLineNameException extends DuplicateException {

    private static final String MESSAGE = "중복된 노선 이름입니다.";

    public DuplicateLineNameException() {
        super(MESSAGE);
    }

}
