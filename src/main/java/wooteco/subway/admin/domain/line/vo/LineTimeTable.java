package wooteco.subway.admin.domain.line.vo;

import static wooteco.subway.admin.domain.line.vo.InvalidLineTimeTableException.*;

import java.time.Duration;
import java.time.LocalTime;

public class LineTimeTable {
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int intervalTime;

    public LineTimeTable(LocalTime startTime, LocalTime endTime, int intervalTime) {
        validate(startTime, endTime, intervalTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    private void validate(LocalTime startTime, LocalTime endTime, int intervalTime) {
        if (startTime.isAfter(endTime)) {
            throw new InvalidLineTimeTableException(INVALID_RUNNING_TIME);
        }
        int timeGap = (int) Duration.between(startTime, endTime).getSeconds() / 60;
        if (timeGap < intervalTime) {
            throw new InvalidLineTimeTableException(INVALID_INTERVAL_TIME);
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
