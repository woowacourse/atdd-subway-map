package wooteco.subway.dto.section.response;

import wooteco.subway.domain.Section;

import javax.validation.constraints.NotNull;

public class SectionInsertResponse {
    @NotNull
    private Long id;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private Integer distance;

    public SectionInsertResponse() {
    }

    public SectionInsertResponse(Section section) {
        this(section.getId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public SectionInsertResponse(Long id, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
