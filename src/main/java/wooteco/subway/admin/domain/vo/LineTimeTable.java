package wooteco.subway.admin.domain.vo;

import java.time.LocalTime;
import java.util.Objects;

public class LineTimeTable {

    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    public LineTimeTable(LocalTime startTime, LocalTime endTime, int intervalTime) {
        validate(startTime, endTime, intervalTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    private void validate(LocalTime startTime, LocalTime endTime, int intervalTime) {
        if (Objects.isNull(startTime)) {
            throw new IllegalArgumentException("Line.startTime은 null일 수 없습니다.");
        }
        if (Objects.isNull(endTime)) {
            throw new IllegalArgumentException("Line.endTime은 null일 수 없습니다.");
        }
        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("Line.startTime은 Line.endTime보다 늦을 수 없습니다.");
        }
        if (intervalTime <= 0) {
            throw new IllegalArgumentException("Line.inttervalTime은 0이하일 수 없습니다.");
        }
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }
}
