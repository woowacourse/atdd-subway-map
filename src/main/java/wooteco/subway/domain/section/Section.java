package wooteco.subway.domain.section;

public class Section {

    private static final String CAN_NOT_CREATE_SECTION = "노선을 생성할 수 없습니다.";
    private static final String DISTANCE_EXCEPTION = "추가하려는 구간이 기존 역 사이 길이보다 크거나 같습니다.";

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isSameAsUpStation(Long id) {
        return this.upStationId.equals(id);
    }

    public boolean isSameAsDownStation(Long id) {
        return this.downStationId.equals(id);
    }

    public Section createExceptSection(Section section) {
        validateCreatableExceptSection(section);
        if (isSameAsUpStation(section.upStationId)) {
            return new Section(lineId, section.downStationId, downStationId, distance - section.getDistance());
        }
        return new Section(lineId, upStationId, section.upStationId, distance - section.getDistance());
    }

    private void validateCreatableExceptSection(Section section) {
        if (distance <= section.distance) {
            throw new IllegalStateException(DISTANCE_EXCEPTION);
        }
        if (!isSameAsUpStation(section.upStationId) && !isSameAsDownStation(section.downStationId)) {
            throw new IllegalArgumentException(CAN_NOT_CREATE_SECTION);
        }
    }

    public Section createCombinedSection(Section section) {
        validateAbleToCombine(section);
        if (isSameAsUpStation(section.downStationId)) {
            return new Section(lineId, section.upStationId, downStationId, distance + section.distance);
        }
        return new Section(lineId, upStationId, section.downStationId, distance + section.distance);
    }

    private void validateAbleToCombine(Section section) {
        if (!isSameAsUpStation(section.downStationId) && !isSameAsDownStation(section.upStationId)) {
            throw new IllegalArgumentException(CAN_NOT_CREATE_SECTION);
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
