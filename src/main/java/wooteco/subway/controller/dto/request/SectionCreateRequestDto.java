package wooteco.subway.controller.dto.request;

public class SectionCreateRequestDto {
    private int distance;
    private Long downStationId;
    private Long upStationId;

    public SectionCreateRequestDto() {
    }

    public SectionCreateRequestDto(int distance, Long downStationId, Long upStationId) {
        this.distance = distance;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }
}
