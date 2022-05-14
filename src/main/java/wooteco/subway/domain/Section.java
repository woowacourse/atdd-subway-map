package wooteco.subway.domain;

public class Section {

    private Long id;
    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;
    private Long lineOrder;

    public Section() {
    }

    public Section(Long lineOrder) {
        this.lineOrder = lineOrder;
    }

    public Section(Long id, long lineId, long upStationId, long downStationId, int distance, Long lineOrder) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineOrder = lineOrder;
    }

    public static Section createOf(Long id, long lineId, long upStationId, long downStationId,
                                   int distance, Long lineOrder) {
        return new Section(id, lineId, upStationId, downStationId, distance, lineOrder);
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

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameUpStationId(long stationId) {
        return upStationId == stationId;
    }

    public boolean isSameDownStationId(long stationId) {
        return downStationId == stationId;
    }
}
