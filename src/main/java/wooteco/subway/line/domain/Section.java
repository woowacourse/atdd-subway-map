package wooteco.subway.line.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(){}

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(0L, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Long lineId() {
        return lineId;
    }

    public Long upsStationId() {
        return upStationId;
    }

    public Long downStationId() {
        return downStationId;
    }

    public int distance() {
        return distance;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
