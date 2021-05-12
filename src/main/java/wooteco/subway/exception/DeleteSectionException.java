package wooteco.subway.exception;

public class DeleteSectionException extends RuntimeException {

    private static final String MESSAGE = "구간을 삭제할 수 없습니다.";

    public DeleteSectionException() {
        super(MESSAGE);
    }

}
