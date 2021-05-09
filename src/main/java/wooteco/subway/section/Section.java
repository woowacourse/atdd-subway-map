package wooteco.subway.section;

public class Section {
    private final long upStationId;
    private final long downStationId;
    private long lineId;

    public Section(long upStationId, long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(long lineId, long upStationId, long downStationId) {
        this(upStationId, downStationId);
        this.lineId = lineId;
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
}
