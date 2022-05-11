package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, null, upStationId, downStationId, distance);
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
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

    public boolean findByUpStationId(Long id) {
        return upStationId.equals(id);
    }

    public boolean findByDownStationId(Long id) {
        return downStationId.equals(id);
    }

    public boolean isSameUpByDown(Section section) {
        return Objects.equals(upStationId, section.getDownStationId());
    }

    public boolean isSameDownByUp(Section section) {
        return Objects.equals(downStationId, section.getUpStationId());
    }

    public boolean isSameUpStation(Section section) {
        return Objects.equals(upStationId, section.getUpStationId());
    }

    public boolean isSameDownStation(Section section) {
        return Objects.equals(downStationId, section.getDownStationId());
    }

    public boolean isLongDistance(Section section) {
        return distance > section.getDistance();
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

        if (distance != section.distance) {
            return false;
        }
        if (!upStationId.equals(section.upStationId)) {
            return false;
        }
        return downStationId.equals(section.downStationId);
    }

    @Override
    public int hashCode() {
        int result = upStationId.hashCode();
        result = 31 * result + downStationId.hashCode();
        result = 31 * result + distance;
        return result;
    }
}
