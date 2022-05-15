package wooteco.subway.exception;

public class BothUpAndDownStationExistException extends RuntimeException {

    public BothUpAndDownStationExistException() {
        super("추가하려는 구간의 상행선과 하행선 모두 이미 구간 목록에 존재하여 추가할 수 없습니다.");
    }
}
