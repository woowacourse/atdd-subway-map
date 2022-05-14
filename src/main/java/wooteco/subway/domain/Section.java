package wooteco.subway.domain;

import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.SubwayException;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public Section(Long upStationId, Long downStationId, Long distance) {
        validateSelfLoop(upStationId, downStationId);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Long distance) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Long distance) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
        this.id = id;
    }

    private void validateSelfLoop(Long upStationId, Long downStationId) {
        if (upStationId == downStationId.longValue()) {
            throw new SubwayException("[ERROR] 상행역과 하행역이 같을 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getDistance() {
        return distance;
    }
}
