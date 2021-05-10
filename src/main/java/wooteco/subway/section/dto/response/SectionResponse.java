package wooteco.subway.section.dto.response;

import wooteco.subway.section.Section;

public class SectionResponse {
    private Long upStationId;
    private Long downStationId;

    public SectionResponse(Section section) {
        this(section.getUpStationId(), section.getDownStationId());
    }

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
