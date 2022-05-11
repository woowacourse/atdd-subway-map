package wooteco.subway.domain;

public class Section {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section divideRight(final Section section) {
        return new Section(section.downStationId, downStationId, distance - section.distance);
    }

    public Section divideLeft(final Section section) {
        return new Section(upStationId, section.upStationId, distance - section.distance);
    }

    public boolean isConnected(final Section section) {
        return downStationId.equals(section.upStationId);
    }

    public boolean equalsUpStation(final Section section) {
        return upStationId.equals(section.upStationId);
    }

    public boolean equalsDownStation(final Section section) {
        return downStationId.equals(section.downStationId);
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
