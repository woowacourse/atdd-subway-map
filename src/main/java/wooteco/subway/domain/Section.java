package wooteco.subway.domain;

public class Section {

    private static final String DISTANCE_EXCEPTION = "추가하려는 구간이 기존 역 사이 길이보다 크거나 같습니다.";
    private static final String CAN_NOT_COMBINE_SECTION = "노선을 결합할 수 없습니다.";

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

    public boolean isSameUpStation(Long upStationId) {
        return this.upStationId.equals(upStationId);
    }

    public boolean isSameDownStation(Long downStationId) {
        return this.downStationId.equals(downStationId);
    }

    public Section createExceptSection(Section section) {
        if (upStationId.equals(section.upStationId)) {
            return createExceptUpSection(section);
        }
        if (downStationId.equals(section.downStationId)) {
            return createExceptDownSection(section);
        }
        throw new IllegalArgumentException();
    }

    public Section createExceptDownSection(Section section) {
        validateDistance(section);
        return new Section(
            lineId,
            upStationId,
            section.upStationId,
            distance - section.getDistance()
        );
    }

    public Section createExceptUpSection(Section section) {
        validateDistance(section);
        return new Section(
            lineId,
            section.downStationId,
            downStationId,
            distance - section.getDistance()
        );
    }

    private void validateDistance(Section section) {
        if (distance <= section.distance) {
            throw new IllegalStateException(DISTANCE_EXCEPTION);
        }
    }

    public Section createCombineSection(Section section) {
        if (upStationId.equals(section.downStationId)) {
            return new Section(lineId, section.upStationId, downStationId, distance + section.distance);
        }
        if (downStationId.equals(section.upStationId)) {
            return new Section(lineId, upStationId, section.downStationId, distance + section.distance);
        }
        throw new IllegalArgumentException(CAN_NOT_COMBINE_SECTION);
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
