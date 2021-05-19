package wooteco.subway.section.service.dto;

import wooteco.subway.line.service.dto.LineCreateDto;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;

public class SectionCreateDto {

    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final boolean isOfNewLine;

    private SectionCreateDto(final Long lineId, final Long upStationId, final Long downStationId, final int distance, final boolean isOfNewLine) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.isOfNewLine = isOfNewLine;
    }

    public static SectionCreateDto ofNewLine(final Line line, final LineCreateDto lineInfo) {
        return new SectionCreateDto(
                line.getId(),
                lineInfo.getUpStationId(),
                lineInfo.getDownStationId(),
                lineInfo.getDistance(),
                true
        );
    }

    public static SectionCreateDto ofExistingLine(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        return new SectionCreateDto(
                lineId,
                upStationId,
                downStationId,
                distance,
                false
        );
    }

    public Section toSection() {
        return new Section(
                lineId,
                upStationId,
                downStationId,
                distance
        );
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

    public boolean isOfNewLine() {
        return isOfNewLine;
    }
}
