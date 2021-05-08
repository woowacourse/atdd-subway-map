package wooteco.subway.controller.dto.response.section;

import wooteco.subway.domain.Section;

public class SectionCreateResponseDto {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionCreateResponseDto() {
    }

    public SectionCreateResponseDto(Long id, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionCreateResponseDto(Section section) {
        this(section.getId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Long getId() {
        return id;
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
