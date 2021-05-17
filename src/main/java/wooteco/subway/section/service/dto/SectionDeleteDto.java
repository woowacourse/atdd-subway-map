package wooteco.subway.section.service.dto;

public class SectionDeleteDto {

    private final Long lineId;
    private final Long stationId;

    private SectionDeleteDto(final Long lineId, final Long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public static SectionDeleteDto of(final Long lineId, final Long stationId) {
        return new SectionDeleteDto(lineId, stationId);
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }
}
