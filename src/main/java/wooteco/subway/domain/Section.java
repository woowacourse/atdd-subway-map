package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public boolean isExistSameStation(Section anotherSection) {
        return (upStationId == anotherSection.upStationId) || (upStationId == anotherSection.downStationId)
                || (downStationId == anotherSection.upStationId) || (downStationId == anotherSection.downStationId);
    }

    public void update(Long inputUpStationId, Long inputDownStationId, int distance) {
        this.upStationId = inputUpStationId;
        this.downStationId = inputDownStationId;
        this.distance = distance;
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
        return Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId,
                section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId);
    }
}
