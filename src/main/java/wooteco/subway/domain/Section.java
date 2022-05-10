package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, upStationId, downStationId, distance);
    }

    public Section(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }
}
