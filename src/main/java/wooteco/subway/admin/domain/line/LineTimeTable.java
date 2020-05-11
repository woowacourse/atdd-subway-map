package wooteco.subway.admin.domain.line;

import java.time.LocalTime;

public class LineTimeTable {
    private LocalTime startTime;
    private LocalTime endTime;

    public LineTimeTable(LocalTime startTime, LocalTime endTime) {
        validate(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void validate(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("막차 시간은 첫차 시간 보다 빠를수 없습니다.");
        }
    }
}
