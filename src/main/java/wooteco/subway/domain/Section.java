package wooteco.subway.domain;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

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

    public boolean isLong(Section section) {
        return this.distance > section.distance;
    }

    public boolean isSameUpStation(Long upStationId) {
        return this.upStationId.equals(upStationId);
    }

    public boolean isSameDownStation(Long downStationId) {
        return this.downStationId.equals(downStationId);
    }

    public Section createExceptDownSection(Section section) {
        return new Section(
            lineId,
            upStationId,
            section.upStationId,
            distance - section.getDistance()
        );
    }

    public Section createExceptUpSection(Section section) {
        return new Section(
            lineId,
            section.downStationId,
            downStationId,
            distance - section.getDistance()
        );
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
}
