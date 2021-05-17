package wooteco.subway.section.dao;

public class SectionDto {
    private final long id;
    private final long upStationId;
    private final long downStationId;
    private final long distance;

    public SectionDto(long id, long upStationId, long downStationId, long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}
