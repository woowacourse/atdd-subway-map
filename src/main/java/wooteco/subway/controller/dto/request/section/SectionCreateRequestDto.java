package wooteco.subway.controller.dto.request.section;

public class SectionCreateRequestDto {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionCreateRequestDto() {
    }

    public SectionCreateRequestDto(Long upStationId, Long downStationId, Integer distance) {
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
