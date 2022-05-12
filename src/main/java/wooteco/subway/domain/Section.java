package wooteco.subway.domain;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateStationId(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validateStationId(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException("상행과 하행은 서로 다른 역이어야 합니다.");
        }
    }

    public boolean matchUpStationWithUpStationOf(Section newSection) {
        return this.upStationId == newSection.upStationId;
    }

    public boolean matchUpStationWithDownStationOf(Section newSection) {
        return this.upStationId == newSection.downStationId;
    }

    public boolean matchDownStationWithUpStationOf(Section newSection) {
        return this.downStationId == newSection.upStationId;
    }

    public boolean matchDownStationWithDownStationOf(Section newSection) {
        return this.downStationId == newSection.downStationId;
    }

    public boolean isLessThan(Section newSection) {
        return distance <= newSection.distance;
    }

    public void updateDownStationId(Section newSection) {
        this.downStationId = newSection.upStationId;
    }

    public void updateUpStationId(Section newSection) {
        this.upStationId = newSection.downStationId;
    }

    public void updateDistance(Section newSection) {
        this.distance = this.distance - newSection.distance;
    }

    public boolean containsStation(Long stationId) {
        return this.upStationId == stationId || this.downStationId == stationId;
    }

    public Section merge(Section nextSection) {
        return new Section(lineId, upStationId, nextSection.downStationId,
                distance + nextSection.distance);
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
