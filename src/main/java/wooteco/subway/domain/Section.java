package wooteco.subway.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long lineId, final Long stationId) {
        this(null, lineId, stationId, stationId, 0);
    }

    @JsonCreator
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

    public Section updateDownStationId(final Long downStationId) {
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public Section updateUpStationId(final Long upStationId) {
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public Section updateDistance(final int distance) {
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public boolean isUpStationIdEquals(final Section section) {
        return this.upStationId.equals(section.getUpStationId());
    }

    public boolean isDownStationIdEquals(final Section section) {
        return this.downStationId.equals(section.getDownStationId());
    }
}
