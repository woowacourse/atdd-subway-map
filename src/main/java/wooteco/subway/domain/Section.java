package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;

public class Section {
    private Long id;
    private int distance;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;

    public Section(int distance, Long lineId, Long upStationId, Long downStationId) {
        this.distance = distance;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long id, int distance, Long lineId, Long upStationId, Long downStationId) {
        this.id = id;
        this.distance = distance;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static Section of(Line savedLine, LineRequest lineRequest) {
        return new Section(
                lineRequest.getDistance(),
                savedLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId()
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
}
