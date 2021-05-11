package wooteco.subway.section.domain;

import wooteco.subway.section.exception.SectionIllegalArgumentException;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private Section(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        validateIfDownStationSameAsUpStation(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return new Section(null,
            lineId,
            upStationId,
            downStationId,
            distance);
    }

    public static Section of(Long id, Long lineId, Long upStationId, Long downStationId,
        Integer distance) {
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

    public boolean equalWithUpStationId(Long upStationId) {
        return this.upStationId.equals(upStationId);
    }

    public boolean equalWithDownStationId(Long downStationId) {
        return this.downStationId.equals(downStationId);
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
}
