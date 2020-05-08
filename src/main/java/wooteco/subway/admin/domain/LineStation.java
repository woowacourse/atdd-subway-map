package wooteco.subway.admin.domain;

import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class LineStation {
    // TODO: 테이블 컬럼명과 변수명이 다른 경우
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
        this.distance = 0;
        this.duration = 0;
        this.createdAt = LocalTime.now();
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

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }
}
