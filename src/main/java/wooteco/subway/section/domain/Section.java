package wooteco.subway.section.domain;

import java.util.Objects;
import wooteco.subway.exception.SectionDistanceException;

public class Section {

    private static final int DISTANCE_MIN = 1;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private int distance;
    private Long id;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validateDistance(distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    public Section(Long lineId, Long upStationId, Long downStationId) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    private void validateDistance(int distance) {
        if (distance < DISTANCE_MIN) {
            throw new SectionDistanceException();
        }
    }

    public boolean isSameStations(Section section) {
        return upStationId.equals(section.upStationId)
            && downStationId.equals(section.downStationId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id)
            && Objects.equals(lineId, section.lineId) && Objects
            .equals(upStationId, section.upStationId) && Objects
            .equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
