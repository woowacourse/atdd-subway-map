package wooteco.subway.domain;

public class Section {

    private final Id id;
    private final Id lineId;
    private final Id upStatinoId;
    private final Id downStationId;
    private final Distance distance;

    public Section(final Id id, final Id lineId, final Id upStatinoId, final Id downStationId,
        final Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStatinoId = upStatinoId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id.getValue();
    }

    public Long getLineId() {
        return lineId.getValue();
    }

    public Long getUpStatinoId() {
        return upStatinoId.getValue();
    }

    public Long getDownStationId() {
        return downStationId.getValue();
    }

    public int getDistance() {
        return distance.getValue();
    }
}
