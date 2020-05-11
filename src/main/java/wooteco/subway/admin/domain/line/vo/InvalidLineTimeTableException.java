package wooteco.subway.admin.domain.line.vo;

public class InvalidLineTimeTableException extends IllegalArgumentException {
    static final String INVALID_RUNNING_TIME =  "막차 시간은 첫차 시간 보다 빠를수 없습니다.";
    static final String INVALID_INTERVAL_TIME =  "배차 간격은 총 운행 시간 보다 클 수 없습니다.";

    public InvalidLineTimeTableException(String s) {
        super(s);
    }
}
