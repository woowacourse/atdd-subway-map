package wooteco.subway.domain;

import lombok.Getter;

@Getter
public class Section {
    private static final String SAME_UP_AND_DOWN_STATION_ERROR = "상행과 하행의 지하철 역이 같을 수 없습니다.";
    private static final String NON_POSITIVE_DISTANCE_ERROR = "거리는 양수여야 합니다.";

    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    private Section(Long id, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long id, Long upStationId, Long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        return new Section(id, upStationId, downStationId, distance);
    }

    public static Section of(Long upStationId, Long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        return new Section(0L, upStationId, downStationId, distance);
    }

    public boolean isSameUpStationId(Section section) {
        return upStationId.equals(section.upStationId);
    }

    public boolean isSameDownStationId(Section section) {
        return downStationId.equals(section.downStationId);
    }

    public boolean isSameDownStationId(long id) {
        return downStationId.equals(id);
    }

    public void checkDistanceIsLongerThan(Section section) {
        if (distance <= section.distance) {
            throw new IllegalArgumentException("기존 역 사이보다 긴 길이를 등록할 수 없습니다.");
        }
    }

    private static void validate(Long upStationId, Long downStationId, int distance) {
        validateStationIds(upStationId, downStationId);
        validateDistance(distance);
    }

    private static void validateStationIds(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException(SAME_UP_AND_DOWN_STATION_ERROR);
        }
    }

    private static void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(NON_POSITIVE_DISTANCE_ERROR);
        }
    }
}
