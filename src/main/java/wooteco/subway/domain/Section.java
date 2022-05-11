package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final Long lineId;

    public Section(final Long id, final Long upStationId, final Long downStationId, final int distance, final Long lineId) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(final Long upStationId, final Long downStationId, final int distance, final Long lineId) {
        this(null, upStationId, downStationId, distance, lineId);
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
}
