package wooteco.subway.section.domain;

import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId) {
        this(lineId, upStationId, downStationId, 0);
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(0L, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public boolean isShorterOrEqualTo(final Section that) {
        return this.distance <= that.distance;
    }

    public int getDistanceGap(final Section that) {
        return Math.abs(this.distance - that.distance);
    }

    public boolean hasSameUpStation(final Section that) {
        return this.upStationId.equals(that.upStationId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return distance == section.distance && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
