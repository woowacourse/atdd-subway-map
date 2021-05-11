package wooteco.subway.line.section;

import java.util.Objects;
import wooteco.subway.exception.service.ValidationFailureException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void validateSmaller(final int distance) {
        if (this.distance <= distance) {
            throw new ValidationFailureException("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.");
        }
    }

    public Section createUpdatedSection(final Long upStationId, final Long downStationId, final int distance) {
        if (this.upStationId.equals(upStationId)) {
            return new Section(id, lineId, downStationId, this.downStationId, this.distance - distance);
        }
        return new Section(id, lineId, this.upStationId, upStationId, this.distance - distance);
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
        return distance == section.distance && Objects.equals(id, section.id) && Objects
            .equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects
            .equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
