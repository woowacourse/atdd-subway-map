package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isLinkedToUpStation(Section other) {
        return upStationId == other.downStationId;
    }

    public boolean isLinkedToDownStation(Section other) {
        return downStationId == other.upStationId;
    }

    public boolean hasStation(Long stationId) {
        return (upStationId == stationId || downStationId == stationId);
    }

    public boolean ableToLink(Section newSection) {
        return isLinkedToUpStation(newSection) || isLinkedToDownStation(newSection);
    }

    public boolean isSameUpStation(Section other) {
        return upStationId == other.upStationId;
    }

    public boolean isSameDownStation(Section other) {
        return downStationId == other.downStationId;
    }

    public boolean ableToDivide(Section newSection) {
        return (isSameUpStation(newSection) != isSameDownStation(newSection)) && (distance > newSection.distance);
    }

    public List<Section> divide(Section newSection) {
        List<Section> parts = new ArrayList<>();
        if (upStationId == newSection.getUpStationId()) {
            parts.add(Section.of(lineId, upStationId, newSection.downStationId, newSection.distance));
            parts.add(Section.of(lineId, newSection.downStationId, downStationId, distance - newSection.distance));
            return parts;
        }
        parts.add(Section.of(lineId, upStationId, newSection.upStationId, newSection.distance));
        parts.add(Section.of(lineId, newSection.upStationId, downStationId, distance - newSection.distance));
        return parts;
    }

    public Section merge(Section other) {
        checkAbleToMerge(other);
        if (downStationId == other.upStationId) {
            return new Section(null, lineId, upStationId, other.downStationId, distance + other.distance);
        }
        return new Section(null, lineId, other.upStationId, downStationId, distance + other.distance);
    }

    private void checkAbleToMerge(Section other) {
        if (!(isLinkedToUpStation(other) || isLinkedToUpStation(other))) {
            throw new IllegalArgumentException("합칠 수 없는 section입니다.");
        }
    }
}
