package wooteco.subway.service.dto.section;

public class SectionRequestDto {

    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequestDto() {
    }

    public SectionRequestDto(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
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
