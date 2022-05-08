package wooteco.subway.dto.section;

public class SectionCreationRequest {

    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionCreationRequest() {
    }

    public SectionCreationRequest(final Long lineId, final Long upStationId, final Long downStationId,
                                  final int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
