package wooteco.subway.dto.section;

public class SectionResponse {

    private final Long upStationId;
    private final Long downStationId;

    public SectionResponse(Long upStationId, Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
