package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행역과 하행역이 일치합니다.");
        }
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, null, upStationId, downStationId, distance);
    }

    public Section(Section section, int distance) {
        this(section.getId(), section.getLineId(), section.getUpStationId(), section.getDownStationId(), distance);
    }

    public Section createDivideDownSection(Section section) {
        return new Section(id, lineId, section.getDownStationId(), downStationId, distance - section.getDistance());
    }

    public Section createDivideUpSection(Section section) {
        return new Section(id, lineId, upStationId, section.getUpStationId(), distance - section.getDistance());
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

    public boolean isShorterThan(Section section) {
        return distance < section.distance;
    }

    public boolean equalsUpStation(Section section) {
        return upStationId.equals(section.getUpStationId());
    }

    public boolean equalsUpStation(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean equalsDownStation(Section section) {
        return downStationId.equals(section.getDownStationId());
    }

    public boolean equalsDownStation(Long stationId) {
        return downStationId.equals(stationId);
    }

    public Section merge(Section section) {
        int totalDistance = distance + section.getDistance();
        if (equalsDownStation(section.upStationId)) {
            return new Section(lineId, upStationId, section.downStationId, totalDistance);
        }
        return new Section(lineId, section.upStationId, downStationId, totalDistance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
