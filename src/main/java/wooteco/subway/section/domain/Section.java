package wooteco.subway.section.domain;

import wooteco.subway.exception.NullInputException;
import wooteco.subway.exception.section.InvalidDistanceException;

import java.util.Objects;

public class Section {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long id, Long upStationId, Long downStationId, int distance) {
        validateNotNull(upStationId, downStationId);
        validateDistanceSize(distance);
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, upStationId, downStationId, distance);
    }

    private void validateNotNull(Long upStationId, Long downStationId) {
        if (upStationId == null || downStationId == null) {
            throw new NullInputException();
        }
    }

    private void validateDistanceSize(int distance) {
        if (distance <= 0) {
            throw new InvalidDistanceException();
        }
    }

    public boolean isUpStation(Long id) {
        return upStationId.equals(id);
    }

    public boolean isDownStation(Long id) {
        return downStationId.equals(id);
    }

    public boolean isSameOrLongDistance(Section newSection) {
        return this.distance <= newSection.distance;
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

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
