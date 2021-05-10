package wooteco.subway.line.section;

public class SectionResponse {

    private Long id;
    private Long downStationId;
    private Long upStationId;
    private int distance;

    public SectionResponse() {}

    public SectionResponse(Long id, Long downStationId, Long upStationId, int distance) {
        this.id = id;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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
}
