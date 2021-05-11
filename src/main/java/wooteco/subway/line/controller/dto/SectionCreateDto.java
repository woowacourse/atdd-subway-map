package wooteco.subway.line.controller.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;

public class SectionCreateDto {

    private final Long lineId;
    private final Long downStationId;
    private final Long upStationId;
    private final int distance;
    private final boolean isOfNewLine;

    private SectionCreateDto(final Long lineId, final Long downStationId, final Long upStationId, final int distance, final boolean isOfNewLine) {
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
        this.isOfNewLine = isOfNewLine;
    }

    public static SectionCreateDto ofNewLine(final Line line, final LineCreateDto lineInfo) {
        return new SectionCreateDto(
                line.getId(),
                lineInfo.getDownStationId(),
                lineInfo.getUpStationId(),
                lineInfo.getDistance(),
                true
        );
    }

    public static SectionCreateDto ofExistingLine(final Long lineId, final Long downStationId, final Long upStationId, final int distance) {
        return new SectionCreateDto(
                lineId,
                downStationId,
                upStationId,
                distance,
                false
        );
    }

    public Section toSection() {
        return new Section(
                lineId,
                downStationId,
                upStationId,
                distance
        );
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isOfNewLine() {
        return isOfNewLine;
    }
}
