package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;
import wooteco.subway.exception.ExceptionMessage;

public class Section {

    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public Section(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public List<Section> insert(Section other) {
        checkStationsNotSame(other);
        if (isForAdd(other)) {
            return add(other);
        }
        if (isForDivide(other)) {
            return divide(other);
        }
        throw new IllegalArgumentException(ExceptionMessage.INSERT_SECTION_NOT_MATCH.getContent());
    }

    private void checkStationsNotSame(Section other) {
        if (this.upStationId == other.upStationId && this.downStationId == other.downStationId) {
            throw new IllegalArgumentException(ExceptionMessage.SAME_STATIONS_SECTION.getContent());
        }
    }

    private boolean isForAdd(Section other) {
        return other.upStationId == this.downStationId || other.downStationId == this.upStationId;
    }

    private boolean isForDivide(Section other) {
        return other.downStationId == this.downStationId || other.upStationId == this.upStationId;
    }

    private List<Section> divide(Section other) {
        if (this.distance <= other.distance) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_INSERT_SECTION_DISTANCE.getContent());
        }
        Section dividedSection = new Section(other.downStationId, this.downStationId, this.distance - other.distance);
        return List.of(other, dividedSection);
    }

    private List<Section> add(Section other) {
        return List.of(this, other);
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
        return upStationId == section.upStationId && downStationId == section.downStationId
                && distance == section.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
