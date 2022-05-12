package wooteco.subway.domain;

import java.util.List;
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
        validateField();
    }

    public Section(Long upStationId, Long downStationId, Long lineId, int distance) {
        this(0L, upStationId, downStationId, lineId, distance);
    }

    private void validateField() {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("구간에서 상행선과 하행선은 같은 역으로 할 수 없습니다.");
        }

        if (distance < 1) {
            throw new IllegalArgumentException("상행선과 하행선의 거리는 1 이상이어야 합니다.");
        }
    }

    public boolean beIncludedInUpStation(List<Long> stationsId) {
        return stationsId.contains(upStationId);
    }

    public boolean beIncludedInDownStation(List<Long> stationsId) {
        return stationsId.contains(downStationId);
    }

    public boolean isEqualOfUpStation(Section section) {
        return upStationId.equals(section.upStationId);
    }

    public boolean isEqualOfDownStation(Section section) {
        return downStationId.equals(section.downStationId);
    }

    public Section getCutDistanceSection(Section section) {
        int cutDistance = this.distance - section.distance;
        if(cutDistance <= 0) {
            throw new IllegalArgumentException("이미 존재하는 구간의 거리보다 거리가 길거나 같습니다.");
        }
        if (upStationId.equals(section.upStationId)) {
            return new Section(section.downStationId, downStationId, lineId, cutDistance);
        }
        return new Section(upStationId, section.upStationId, lineId, cutDistance);
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
