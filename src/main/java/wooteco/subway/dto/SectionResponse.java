package wooteco.subway.dto;

public class SectionResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionResponse() {
    }

    public SectionResponse(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
