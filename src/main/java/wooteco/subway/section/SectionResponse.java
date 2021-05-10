package wooteco.subway.section;

public class SectionResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long id, SectionRequest sectionRequest) {
        this.id = id;
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
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
