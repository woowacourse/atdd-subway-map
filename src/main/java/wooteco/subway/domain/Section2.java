package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.entity.SectionEntity;

public class Section2 {

    private static final String SAME_STATION_INPUT_EXCEPTION = "서로 다른 두 개의 역을 입력해야 합니다.";

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section2(Long upStationId, Long downStationId, int distance) {
        validateDifferentStations(upStationId, downStationId);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateDifferentStations(Long upStationId,
                                          Long downStationId     ) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException(SAME_STATION_INPUT_EXCEPTION);
        }
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

    public List<Long> getStationIds() {
        return List.of(upStationId, downStationId);
    }

    public SectionEntity toEntityOf(Long lineId) {
        return new SectionEntity(lineId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
