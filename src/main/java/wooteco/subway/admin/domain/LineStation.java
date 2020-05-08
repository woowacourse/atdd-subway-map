package wooteco.subway.admin.domain;

import org.springframework.data.relational.core.mapping.Column;

public class LineStation {
    // TODO: 테이블 컬럼명과 변수명이 다른 경우
    @Column("line")
    private Long lineId;
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long lineId, Long stationId, Long preStationId, int distance, int duration) {
        this(preStationId, stationId, distance, duration);
        this.lineId = lineId;
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStation of(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
        return new LineStation(lineId, preStationId, stationId, distance, duration);
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

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }
}
