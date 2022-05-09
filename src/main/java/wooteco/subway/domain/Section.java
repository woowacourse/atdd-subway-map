package wooteco.subway.domain;

public class Section {

    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private Long lineOrder;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance, null);
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance, Long lineOrder) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineOrder = lineOrder;
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

    public Long getLineOrder() {
        return lineOrder;
    }
}
