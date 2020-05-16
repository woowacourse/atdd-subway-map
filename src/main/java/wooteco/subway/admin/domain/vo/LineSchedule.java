package wooteco.subway.admin.domain.vo;

import java.time.LocalTime;

public class LineSchedule {

    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    public LineSchedule(LocalTime startTime, LocalTime endTime, int intervalTime) {
        validate(startTime, endTime, intervalTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public static LineSchedule of(LocalTime startTime, LocalTime endTime, int intervalTime) {
        return new LineSchedule(startTime, endTime, intervalTime);
    }

    private void validate(LocalTime startTime, LocalTime endTime, int intervalTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("첫차 시간이 막차 시간보다 이후 일 수 없습니다.");
        }
        if (intervalTime < 0) {
            throw new IllegalArgumentException("배차 시간은 음수일 수 없습니다.");
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
