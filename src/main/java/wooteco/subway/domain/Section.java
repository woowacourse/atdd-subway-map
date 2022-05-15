package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final long DEFAULT_SESSION_ID = 0L;

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(DEFAULT_SESSION_ID, lineId, upStationId, downStationId, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isSameUpStationId(Long stationId) {
        return this.upStationId.equals(stationId);
    }

    public boolean isSameDownStationId(Long stationId) {
        return this.downStationId.equals(stationId);
    }

    public boolean isContainStationId(Long stationId) {
        return this.downStationId.equals(stationId) || this.upStationId.equals(stationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }

    public boolean isMoreDistance(Section currentSection) {
        return distance >= currentSection.distance;
    }
}
