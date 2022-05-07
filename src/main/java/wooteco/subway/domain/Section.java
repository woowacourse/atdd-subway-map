package wooteco.subway.domain;

public class Section {

    public static final int MINIMUM_DISTANCE = 1;
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long id, Section other) {
        return new Section(id, other.lineId, other.upStationId, other.downStationId, other.distance);
    }

    public static Section of(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        validate(upStationId, downStationId, distance);
        return new Section(null, lineId, upStationId, downStationId, distance);
    }

    private static void validate(Long upStationId, Long downStationId, Integer distance) {
        checkUpStationAndDownStationIsDifferent(upStationId, downStationId);
        checkDistanceValueIsValid(distance);
    }

    private static void checkDistanceValueIsValid(Integer distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("종점 사이 거리는 양의 정수여야 합니다.");
        }
    }

    private static void checkUpStationAndDownStationIsDifferent(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException("상행 종점과 하행 종점은 같을 수 없습니다.");
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

    public Integer getDistance() {
        return distance;
    }
}
