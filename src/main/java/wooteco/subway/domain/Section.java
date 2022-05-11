package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Long downStationId;
    private final Long upStationId;
    private final int distance;

    public Section(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getId() {
        return id;
    }
}
