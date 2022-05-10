package wooteco.subway.dto.request;

import wooteco.subway.domain.Section;

public class CreateSectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private CreateSectionRequest() {
    }

    public CreateSectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section toSection(final Long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
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
