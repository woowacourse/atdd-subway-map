package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long lineId, LineRequest lineRequest) {
        this(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validDistance(distance);
        validStationsId(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, SectionRequest sectionRequest) {
        this(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private void validDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이는 0보다 커야합니다.");
        }
    }

    private void validStationsId(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행역과 하행역이 같습니다.");
        }
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
