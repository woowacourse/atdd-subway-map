package wooteco.subway.admin.domain;

import java.time.LocalTime;

public class TimeInfo {
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeInfo(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
