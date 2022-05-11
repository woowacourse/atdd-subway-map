package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.BadRequestLineException;

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
        validateField();
    }

    private void validateField() {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("구간에서 상행선과 하행선은 같은 역으로 할 수 없습니다.");
        }

        if (distance < 1) {
            throw new IllegalArgumentException("상행선과 하행선의 거리는 1 이상이어야 합니다.");
        }
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
