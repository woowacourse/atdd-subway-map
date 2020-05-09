package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table
public class LineStation {
    // TODO: 테이블 컬럼명과 변수명이 다른 경우
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
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

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public void setPreStationId(Long preStationId) {
        this.preStationId = preStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }
}
