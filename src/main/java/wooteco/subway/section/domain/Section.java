package wooteco.subway.section.domain;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Distance distance;

    public Section(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Distance distance) {
        validateSection(lineId, upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateSection(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        validateNull(lineId, upStationId, downStationId, distance);
    }

    private void validateNull(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        Objects.requireNonNull(lineId, "lineId는 null이 될 수 없습니다.");
        Objects.requireNonNull(upStationId, "upStationId는 null이 될 수 없습니다.");
        Objects.requireNonNull(downStationId, "downStationId는 null이 될 수 없습니다.");
        Objects.requireNonNull(distance, "distance는 null이 될 수 없습니다.");
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

    public Distance getDistance() {
        return distance;
    }
}
