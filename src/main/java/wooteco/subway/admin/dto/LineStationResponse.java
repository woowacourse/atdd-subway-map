package wooteco.subway.admin.dto;

import java.util.Objects;

import org.springframework.data.annotation.Id;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {

    @Id
    private Long id;
    private Long lineId;
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long id, Long lineId, Long preStationId, Long stationId,
        int distance, int duration) {
        this.id = id;
        this.lineId = lineId;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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
        LineStationResponse that = (LineStationResponse)o;
        return distance == that.distance &&
            duration == that.duration &&
            Objects.equals(id, that.id) &&
            Objects.equals(lineId, that.lineId) &&
            Objects.equals(preStationId, that.preStationId) &&
            Objects.equals(stationId, that.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, preStationId, stationId, distance, duration);
    }
}
