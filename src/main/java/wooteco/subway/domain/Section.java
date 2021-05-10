package wooteco.subway.domain;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
                   final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section ofLineId(final Long lineId, final Section section) {
        return new Section(null, lineId, section.upStationId, section.downStationId, section.distance);
    }

    public static Section ofSectionId(final Long id, final Section section) {
        return new Section(id, section.lineId, section.upStationId, section.downStationId, section.distance);
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

    public Section subtractDistance(final Section section) {
        return new Section(id, lineId, upStationId, downStationId, section.distance - distance);
    }
}
