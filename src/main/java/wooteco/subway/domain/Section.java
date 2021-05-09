package wooteco.subway.domain;

import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.SectionIllegalArgumentException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    private Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateIfDownStationSameAsUpStation(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(null,
                lineId,
                upStationId,
                downStationId,
                distance);
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
            throw new SectionIllegalArgumentException("구간의 상행과 하행이 같을 수 없습니다.");
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
