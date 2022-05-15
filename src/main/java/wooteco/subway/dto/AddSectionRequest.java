package wooteco.subway.dto;

public class AddSectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public AddSectionRequest() {
    }

    public AddSectionRequest(Long upStationId, Long downStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }
}
