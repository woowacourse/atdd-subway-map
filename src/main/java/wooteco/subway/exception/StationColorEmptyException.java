package wooteco.subway.exception;

public class StationColorEmptyException extends EmptyArgumentException {

    public StationColorEmptyException() {
        super("지하철 노선의 색상명이 공백일 수 없습니다.");
    }
}
