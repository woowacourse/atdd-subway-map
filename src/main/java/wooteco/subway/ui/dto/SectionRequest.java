package wooteco.subway.ui.dto;

import wooteco.subway.domain.Section;

public class SectionRequest {

    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest from(Long lineId, LineCreateRequest lineCreateRequest) {
        return new SectionRequest(lineId, lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId(),
                lineCreateRequest.getDistance());
    }

    public Section toEntity(Long sectionId) {
        return new Section(sectionId, lineId, upStationId, downStationId, distance);
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
