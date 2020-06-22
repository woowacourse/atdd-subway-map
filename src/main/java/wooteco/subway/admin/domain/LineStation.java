package wooteco.subway.admin.domain;

import static wooteco.subway.admin.controller.LineStationController.*;

import java.time.LocalTime;
import java.util.Objects;

import org.springframework.data.relational.core.mapping.Table;

@Table(value = "LINE_STATION")
public class LineStation {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;
    private LocalTime createdAt;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = DEFAULT_DISTANCE;
        this.duration = DEFAULT_DURATION;
        this.createdAt = LocalTime.now();
    }

    public static LineStation generateWithoutPre(Long stationId) {
        return new LineStation(null, stationId);
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LineStation that = (LineStation)o;
        return Objects.equals(stationId, that.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId);
    }
}
