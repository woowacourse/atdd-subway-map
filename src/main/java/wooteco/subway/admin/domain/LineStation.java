package wooteco.subway.admin.domain;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("LINESTATION")
public class LineStation {
    @Id
    private Long id;
    @Column("pre_station_id")
    private Long preStationId;
    @Column("station_id")
    private Long stationId;
    @Column("distance")
    private int distance;
    @Column("duration")
    private int duration;

    public LineStation() {}

    public LineStation(Long id, Long preStationId, Long stationId, int distance, int duration) {
        this.id = id;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public LineStation(Long preStationId, Long stationId, int distance, int duration) {
        this(null, preStationId, stationId, distance, duration);
    }

    public void updatePreStationId(Long newPreStationId) {
        this.preStationId = newPreStationId;
    }

    public boolean isArrivalStationStart() {
        return this.preStationId == null;
    }

    public boolean isStationId(Long stationId) {
        return this.stationId.equals(stationId);
    }

    public boolean isPreStationId(Long preStationId) {
        if (Objects.isNull(preStationId)) {
            throw new IllegalArgumentException("preStationID 가 null 입니다." +
                "도착역이 시작역인지 알고싶으면 isArrivalStationStart 메서드를 활용해보세요.");
        }
        return preStationId != null && preStationId.equals(this.preStationId);
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
}
