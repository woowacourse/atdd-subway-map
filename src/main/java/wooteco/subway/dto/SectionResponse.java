package wooteco.subway.dto;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.entity.SectionEntity;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionResponse() {
    }

    public SectionResponse(Long id, Long lineId, Long upStationId, Long downStationId, Long distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionResponse(Long lineId, Long upStationId, Long downStationId, Long distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionResponse(SectionEntity sectionEntity) {
        this(sectionEntity.getLineId(), sectionEntity.getUpStationId(), sectionEntity.getDownStationId(),
                sectionEntity.getDistance());
    }

    public SectionResponse(Section section) {
        this(section.getId(), section.getUpStation().getId(),
                section.getDownStation().getId(), section.getDistance());
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
