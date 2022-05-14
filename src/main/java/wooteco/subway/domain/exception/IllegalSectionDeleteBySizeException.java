package wooteco.subway.domain.exception;

public class IllegalSectionDeleteBySizeException extends ExpectedException {

    private static final String MESSAGE = "구간이 1개 이하 이므로 삭제할 수 없습니다.";

    public IllegalSectionDeleteBySizeException() {
        super(MESSAGE);
    }
}
