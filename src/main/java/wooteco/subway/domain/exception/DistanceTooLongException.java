package wooteco.subway.domain.exception;

public class DistanceTooLongException extends ExpectedException {

    private static final String MESSAGE = "역 사이에 새로운 역을 등록할 경우 기존 구간 거리보다 적어야 합니다.";

    public DistanceTooLongException() {
        super(MESSAGE);
    }
}
