package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final Long lineId;
    private final int distance;

    public Section(Long id, Long upStationId, Long downStationId, Long lineId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Long lineId, int distance) {
        this(0L, upStationId, downStationId, lineId, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId) && Objects.equals(lineId,
                section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, lineId, distance);
    }
}
