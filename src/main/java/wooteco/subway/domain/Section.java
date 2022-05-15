package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;
    private final Long lineOrder;

    public Section(Long id, long lineId, long upStationId, long downStationId, int distance) {
        this(id, lineId, upStationId, downStationId, distance, 1L);
    }

    public Section(Long id, long lineId, long upStationId, long downStationId, int distance, Long lineOrder) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineOrder = lineOrder;
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineOrder() {
        return lineOrder;
    }
}
