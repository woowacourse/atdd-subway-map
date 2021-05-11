package wooteco.subway.section;

import wooteco.subway.exception.SameStationIdException;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, SectionRequest request){
        this(id, request.getUpStationId(), request.getDownStationId(), request.getDistance());
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validStationIds(upStationId, downStationId);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validStationIds(Long upStationId, Long downStationId) {
        if(upStationId.equals(downStationId)) {
            throw new SameStationIdException();
        }
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
