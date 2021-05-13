package wooteco.subway.section.dto.response;

import wooteco.subway.section.Section;

public class SectionResponse {
    private long upStationId;
    private long downStationId;

    public SectionResponse(Section section) {
        this(section.getUpStationId(), section.getDownStationId());
    }

    public SectionResponse(long upStationId, long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }
}
