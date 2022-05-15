package wooteco.subway.domain;

import java.util.List;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validate(Long upStationId, Long downStationId, int distance) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행 종점과 하행 종점은 같을 수 없습니다.");
        }
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("역간의 거리는 1 이상이어야 합니다.");
        }
    }

    public List<Section> split(Section newSection) {
        int newDistance = validateDistance(newSection.getDistance());
        if (this.upStationId.equals(newSection.getUpStationId())) {
            return List.of(newSection, new Section(lineId, newSection.getDownStationId(), downStationId, newDistance));
        }
        return List.of(new Section(lineId, upStationId, newSection.getUpStationId(), newDistance), newSection);
    }

    private int validateDistance(int distance) {
        if (distance >= this.distance) {
            throw new IllegalArgumentException("기존 구간의 길이보다 작아야합니다.");
        }
        return this.distance - distance;
    }

    public boolean contains(Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    public Section merge(Section section) {
        int totalDistance = this.distance + section.getDistance();
        if (this.downStationId.equals(section.getUpStationId())) {
            return new Section(lineId, this.upStationId, section.getDownStationId(), totalDistance);
        }
        return new Section(lineId, section.getUpStationId(), this.downStationId, totalDistance);
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
