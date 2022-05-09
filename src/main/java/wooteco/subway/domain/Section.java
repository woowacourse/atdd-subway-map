package wooteco.subway.domain;

public class Section {
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
