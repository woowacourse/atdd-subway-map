package wooteco.subway.domain.section;

import java.util.Comparator;
import java.util.Objects;
import wooteco.subway.dto.request.LineRequest;

public class Section implements Comparator<Section> {

    private static final String ERROR_INVALID_DISTANCE = "[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.";
    private static final int INVALID_DISTANCE_STANDARD = 0;
    private static final String ERROR_SAME_STATION = "[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.";

    private final Long id;
    private final Long lineId;
    private final int distance;
    private final Long upStationId;
    private final Long downStationId;

    public Section(final Long id, final LineRequest lineRequest) {
        this(null, id, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(final Long id, final Long lineId,
                   final Long upStationId, final Long downStationId, final int distance) {
        validateStations(upStationId, downStationId);
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateStations(final Long upStationId, final Long downStationId) {
        if (Objects.equals(upStationId, downStationId)) {
            throw new IllegalArgumentException(ERROR_SAME_STATION);
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= INVALID_DISTANCE_STANDARD) {
            throw new IllegalArgumentException(ERROR_INVALID_DISTANCE);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    @Override
    public int compare(final Section section1, final Section section2) {
        return Long.compare(section1.getUpStationId(), section2.getUpStationId());
    }

    public Section createMiddleToDownSection(final Section unSplitSection) {
        return new Section(unSplitSection.id, lineId, downStationId, unSplitSection.getDownStationId(),
            unSplitSection.getDistance() - distance);
    }

    public Section createUpToMiddleSection(final Section unSplitSection) {
        return new Section(unSplitSection.id, lineId, unSplitSection.upStationId, upStationId,
            unSplitSection.getDistance() - distance);
    }

    public Section createUpToDownSection(final Section middleToDownSection) {
        return new Section(id, lineId, upStationId, middleToDownSection.getDownStationId(),
            distance + middleToDownSection.distance);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Section section = (Section) o;

        if (getDistance() != section.getDistance()) {
            return false;
        }
        if (getId() != null ? !getId().equals(section.getId()) : section.getId() != null) {
            return false;
        }
        if (getLineId() != null ? !getLineId().equals(section.getLineId()) : section.getLineId() != null) {
            return false;
        }
        if (getUpStationId() != null ? !getUpStationId().equals(section.getUpStationId())
            : section.getUpStationId() != null) {
            return false;
        }
        return getDownStationId() != null ? getDownStationId().equals(section.getDownStationId())
            : section.getDownStationId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getLineId() != null ? getLineId().hashCode() : 0);
        result = 31 * result + getDistance();
        result = 31 * result + (getUpStationId() != null ? getUpStationId().hashCode() : 0);
        result = 31 * result + (getDownStationId() != null ? getDownStationId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", lineId=" + lineId +
            ", distance=" + distance +
            ", upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            '}';
    }
}
