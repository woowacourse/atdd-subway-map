package wooteco.subway.line.section;

import java.util.Objects;
import wooteco.subway.exception.InvalidInputDataException;

public class Section {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long upStationId, Long downStationId, int distance) {
        this(0L, upStationId, downStationId, distance);
    }

    public Section(Long id, Long upStationId, Long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validate(Long upStationId, Long downStationId, int distance) {
        validateStation(upStationId);
        validateStation(downStationId);
        if (distance <= 0) {
            throw new InvalidInputDataException();
        }
    }

    private void validateStation(Long stationId) {
        if (Objects.isNull(stationId) || stationId == 0L) {
            throw new InvalidInputDataException();
        }
    }

    public boolean isUpEqualDown(Section section) {
        return upStationId.equals(section.getDownStationId());
    }

    public boolean isDownEqualUp(Section section) {
        return downStationId.equals(section.getUpStationId());
    }

    public boolean isUpEqualUp(Section section) {
        return upStationId.equals(section.getUpStationId());
    }

    public boolean isDownEqualDown(Section section) {
        return downStationId.equals(section.getDownStationId());
    }

    public int getDiffDistance(Section addSection) {
        return distance - addSection.getDistance();
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects
            .equals(upStationId, section.upStationId) && Objects
            .equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
