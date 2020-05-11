package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table
public class LineStation {
    // TODO: 테이블 컬럼명과 변수명이 다른 경우
    private Long preStationId;
    private Long stationId;
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

    public LineStation(Long preStationId, Long stationId) {
        this(preStationId, stationId, 0, 0);
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

    public boolean isFirstLineStation() {
        return preStationId == null;
    }
}
