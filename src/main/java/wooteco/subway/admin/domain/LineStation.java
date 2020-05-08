package wooteco.subway.admin.domain;

import org.springframework.data.annotation.Id;

public class LineStation {
    // TODO: 테이블 컬럼명과 변수명이 다른 경우
    @Id
    private Long id;
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(Long id, Long preStationId, Long stationId, int distance, int duration) {
        this.id = id;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getId() {
        return id;
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
