package wooteco.subway.line.domain;

public class Section {
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this.lineId = lineId;
        validateStations(upStationId, downStationId);
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

    private void validateStations(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("출발지와 도착지가 같을 수 없습니다.");
        }
    }
}
