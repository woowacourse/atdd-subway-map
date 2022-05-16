package wooteco.subway.domain.exception;

public class CannotMergeException extends ExpectedException {

    private static final String MESSAGE = "구간 합치기가 불가능합니다.";

    public CannotMergeException() {
        super(MESSAGE);
    }
}
