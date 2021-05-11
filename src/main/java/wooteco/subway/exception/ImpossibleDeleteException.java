package wooteco.subway.exception;

public class ImpossibleDeleteException extends IllegalMethodException {
    private static final String MESSAGE = "구간이 하나 남은 경우 삭제할 수 없습니다.";

    public ImpossibleDeleteException() {
        super(MESSAGE);
    }
}
