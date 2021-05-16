package wooteco.subway.service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class SectionServiceDto {

    private final Long id;
    @NotNull
    private final Long lineId;
    @NotNull
    private final Long upStationId;
    @NotNull
    private final Long downStationId;
    @Min(1)
    private final int distance;

    public SectionServiceDto(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public SectionServiceDto(Long id, Long lineId, Long upStationId, Long downStationId,
        int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionServiceDto from(Section section) {
        return new SectionServiceDto(
            section.getId(),
            section.getLineId(),
            section.getUpStationId(),
            section.getDownStationId(),
            section.getDistanceValue()
        );
    }

    public static SectionServiceDto of(Line line, CreateLineDto dto) {
        return new SectionServiceDto(line.getId(), dto.getUpStationId(), dto.getDownStationId(),
            dto.getDistance());
    }

    public static SectionServiceDto from(CreateSectionDto dto) {
        return new SectionServiceDto(dto.getLineId(), dto.getUpStationId(), dto.getDownStationId(),
            dto.getDistance());
    }

    public Long getId() {
        return id;
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
