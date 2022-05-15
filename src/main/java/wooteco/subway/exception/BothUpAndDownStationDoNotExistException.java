package wooteco.subway.exception;

public class BothUpAndDownStationDoNotExistException extends RuntimeException {

    public BothUpAndDownStationDoNotExistException() {
        super("추가하려는 구간의 상행선과 하행선 모두 구간 목록에 존재하지 않아 구간을 추가할 수 없습니다.");
    }
}
