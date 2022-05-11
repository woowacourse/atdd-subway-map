package wooteco.subway.domain;

import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.ClientException;

public class Section {

    private final Long id;
    private final Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
    
    public boolean isSameUpStationId(SectionRequest request) {
        return upStationId.equals(request.getUpStationId());
    }

    public boolean isSameDownStationId(SectionRequest request) {
        return downStationId.equals(request.getDownStationId());
    }

    public boolean isPossibleDistanceCondition(SectionRequest sectionRequest) {
        return distance > sectionRequest.getDistance();
    }

    public Section createBySameUpStationId(Long id, SectionRequest request) {
        return new Section(0L, id, request.getDownStationId(), downStationId, distance - request.getDistance());
    }

    public Section createBySameDownStationId(Long id, SectionRequest request) {
        return new Section(0L, id, upStationId, request.getUpStationId(), distance - request.getDistance());
    }

    public void updateSameUpStationId(SectionRequest request) {
        this.downStationId = request.getDownStationId();
        this.distance = request.getDistance();
    }

    public void updateSameDownStationId(SectionRequest request) {
        this.upStationId = request.getUpStationId();
        this.distance = request.getDistance();
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
