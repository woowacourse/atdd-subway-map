package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;
import wooteco.subway.exception.ExceptionMessage;

public class Section {

    private Long id;
    private Long line_id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long line_id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.line_id = line_id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long line_id, Long upStationId, Long downStationId, int distance) {
        this.id = null;
        this.line_id = line_id;
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
        Section dividedSection = new Section(this.line_id, other.downStationId, this.downStationId, this.distance - other.distance);
        return List.of(other, dividedSection);
    }

    private List<Section> add(Section other) {
        return List.of(this, other);
    }

    public Section merge(Section other) {
        checkStationsNotSame(other);
        int mergedDistance = this.distance + other.distance;

        if (this.upStationId == other.downStationId) {
            return new Section(this.line_id, other.upStationId, this.downStationId, mergedDistance);
        }
        if (this.downStationId == other.upStationId) {
            return new Section(this.line_id, this.upStationId, other.downStationId, mergedDistance);
        }
        throw new IllegalArgumentException(ExceptionMessage.NOT_CONNECTED_SECTIONS.getContent());
    }

    public Long getId() {
        return id;
    }

    public Long getLine_id() {
        return line_id;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
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
        return distance == section.distance && Objects.equals(line_id, section.line_id)
                && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId,
                section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line_id, upStationId, downStationId, distance);
    }
}
