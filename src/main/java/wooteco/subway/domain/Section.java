package wooteco.subway.domain;

import wooteco.subway.exception.SubwayIllegalArgumentException;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    private Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateArguments(lineId, upStationId, downStationId, distance);
        validateIfDownStationSameAsUpStation(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateArguments(Long lineId, Long upStationId, Long downStationId, int distance) {
        Objects.requireNonNull(lineId);
        Objects.requireNonNull(upStationId);
        Objects.requireNonNull(downStationId);
        if(distance <= 0){
            throw new SubwayIllegalArgumentException("거리는 0보다 커야 합니다.");
        }
    }

    public static Section of(Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(null,
                lineId,
                upStationId,
                downStationId,
                distance);
    }

    public static Section of(Long lineId, Section downSection, Section upSection) {
        return new Section(null,
                lineId,
                downSection.getUpStationId(),
                upSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance());
    }

    public static Section of(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(id,
                lineId,
                upStationId,
                downStationId,
                distance);
    }

    private void validateIfDownStationSameAsUpStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SubwayIllegalArgumentException("구간의 상행과 하행이 같을 수 없습니다.");
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
