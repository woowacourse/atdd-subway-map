package wooteco.subway.exception;

public class OnlyOneSectionException extends RuntimeException {

    public OnlyOneSectionException() {
        super("구간 목록에 구간이 단 하나의 구간만 존재하여 역을 제거할 수 없습니다.");
    }
}
