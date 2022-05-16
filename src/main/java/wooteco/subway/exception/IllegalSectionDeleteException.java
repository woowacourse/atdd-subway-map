package wooteco.subway.exception;

public class IllegalSectionDeleteException extends IllegalArgumentException {
    public IllegalSectionDeleteException() {
        super("구간 삭제 중 예외가 발생 하였습니다.");
    }
}
