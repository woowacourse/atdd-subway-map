package wooteco.subway.domain;

public class Section {

    private Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public boolean hasSameUpStation(Section section) {
        return this.upStationId.equals(section.upStationId);
    }

    public boolean hasSameDownStation(Section section) {
        return this.downStationId.equals(section.downStationId);
    }

    public boolean hasAnySameStation(Section section) {
        return hasSameDownStation(section)
                || hasSameDownStation(section)
                || this.upStationId.equals(section.downStationId)
                || this.downStationId.equals(section.upStationId);
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
