package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final String STATION_DISTANCE_NEGATIVE_ERROR = "두 역간의 거리는 양수여야 합니다.";
    private static final String ALL_STATION_SAME_ERROR = "구간의 두 지하철역은 같을 수 없습니다.";

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateDistance(distance);
        validateDistance(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validateDistance(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException(ALL_STATION_SAME_ERROR);
        }
    }

    private void validateDistance(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException(STATION_DISTANCE_NEGATIVE_ERROR);
        }
    }

    public boolean isExistSameStation(Section anotherSection) {
        return (upStationId == anotherSection.upStationId) || (upStationId == anotherSection.downStationId)
                || (downStationId == anotherSection.upStationId) || (downStationId == anotherSection.downStationId);
    }

    public void update(Long inputUpStationId, Long inputDownStationId, int distance) {
        this.upStationId = inputUpStationId;
        this.downStationId = inputDownStationId;
        this.distance = distance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId,
                section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId);
    }
}
