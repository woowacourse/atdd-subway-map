package wooteco.subway.domain;

public class Section {

    private static final String INVALID_DISTANCE_ERROR_MESSAGE = "유효하지 않은 거리입니다.";
    private static final String DUPLICATED_SECTIONS_ERROR_MESSAGE = "상행과 하행은 같은 역으로 등록할 수 없습니다.";
    private static final int MIN_DISTANCE = 1;

    private Long id;
    private Long lineId;
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

    public void validSection() {
        validDistance();
        validStations();
    }

    private void validDistance() {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }

    private void validStations() {
        if (downStationId.equals(upStationId)) {
            throw new IllegalArgumentException(DUPLICATED_SECTIONS_ERROR_MESSAGE);
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
