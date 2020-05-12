package wooteco.subway.admin.line.domain.edge;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Objects;

public class Edge {
    @Id
    private Long id;
    private Long lineKey;
    private Long stationId;
    private Long preStationId;
    private Integer distance;
    private Integer duration;

    @PersistenceConstructor
    private Edge(final Long id, final Long lineKey, final Long stationId, final Long preStationId, final Integer distance, final Integer duration) {
        this.id = id;
        this.lineKey = lineKey;
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Edge(Long preStationId, Long stationId, Integer distance, Integer duration) {
        validate(preStationId, stationId);
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    private void validate(final Long preStationId, final Long stationId) {
        if (Objects.equals(preStationId, stationId)) {
            throw new IllegalArgumentException(stationId + " : 이전역 값과 대상역 값이 같습니다.");
        }
    }

    public static Edge startEdge(Edge edge) {
        return new Edge(null, edge.preStationId, null, null);
    }

    public boolean isStartStation() {
        return preStationId == null;
    }

    public boolean isNotStartStation() {
        return !isStartStation();
    }

    public boolean hasSamePreStation(final Edge edge) {
        return Objects.equals(this.preStationId, edge.preStationId);
    }

    public void changePreStationToStationOf(final Edge edge) {
        this.lineKey = edge.lineKey;
        this.preStationId = edge.stationId;
    }

    public boolean isSameStationId(final Long stationId) {
        return Objects.equals(this.stationId, stationId);
    }

    public boolean isSamePreStationId(final Long stationId) {
        return Objects.equals(this.preStationId, stationId);
    }

    public void replacePreStation(final Edge edge) {
        this.preStationId = edge.preStationId;
    }

    public boolean isSame(final Edge edge) {
        return Objects.equals(this.preStationId, edge.preStationId) &&
                Objects.equals(this.stationId, edge.stationId);
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }

}
