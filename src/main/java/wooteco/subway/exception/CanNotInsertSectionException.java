package wooteco.subway.exception;

public class CanNotInsertSectionException extends RuntimeException {

    public CanNotInsertSectionException() {
        super("구간 삽입이 불가능합니다.");
    }
}
