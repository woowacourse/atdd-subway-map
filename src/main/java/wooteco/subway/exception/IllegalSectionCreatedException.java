package wooteco.subway.exception;

public class IllegalSectionCreatedException extends IllegalArgumentException {
    public IllegalSectionCreatedException() {
        super("구간 생성 중 예외가 발생하였습니다.");
    }
}
