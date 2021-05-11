package wooteco.subway.domain;

import wooteco.subway.exception.SubwayIllegalArgumentException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Section {
    private final Long id;
    @NotNull(message = "구간의 노선 Id는 필수로 입력하여야 합니다.")
    private final Long lineId;
    @NotNull(message = "구간의 상행역 Id는 필수로 입력하여야 합니다.")
    private final Long upStationId;
    @NotNull(message = "구간의 하행역 Id는 필수로 입력하여야 합니다.")
    private final Long downStationId;
    @Min(value = 1, message = "구간의 거리가 잘못 되었습니다.")
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
