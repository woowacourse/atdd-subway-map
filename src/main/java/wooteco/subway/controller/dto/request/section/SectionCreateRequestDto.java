package wooteco.subway.controller.dto.request.section;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SectionCreateRequestDto {
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
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
