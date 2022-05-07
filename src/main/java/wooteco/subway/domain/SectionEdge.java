package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.NotSplittableSectionException;

public class SectionEdge {

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEdge(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionEdge split(SectionEdge edge) {
        if (isInvalidDistance(edge)) {
            throw new NotSplittableSectionException(edge.getUpStationId(), edge.getDownStationId());
        }
        return new SectionEdge(newUpStationId(edge), newDownStationId(edge), newDistance(edge));
    }

    private boolean isInvalidDistance(SectionEdge edge) {
        return distance <= edge.getDistance();
    }

    private Long newUpStationId(SectionEdge edge) {
        if (upStationId.equals(edge.getUpStationId())) {
            return edge.getDownStationId();
        }
        if (downStationId.equals(edge.getDownStationId())) {
            return upStationId;
        }
        throw new NotSplittableSectionException(edge.getUpStationId(), edge.getDownStationId());
    }

    private Long newDownStationId(SectionEdge edge) {
        if (upStationId.equals(edge.getUpStationId())) {
            return downStationId;
        }
        if (downStationId.equals(getDownStationId())) {
            return edge.getUpStationId();
        }
        throw new NotSplittableSectionException(edge.getUpStationId(), edge.getDownStationId());
    }

    private int newDistance(SectionEdge edge) {
        return this.distance - edge.getDistance();
    }

    public SectionEdge merge(SectionEdge edge) {
        return new SectionEdge(upStationId, edge.getDownStationId(), distance + edge.getDistance());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionEdge that = (SectionEdge) o;
        return distance == that.distance && Objects.equals(upStationId, that.upStationId)
            && Objects.equals(downStationId, that.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionEdge{" +
            "upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            ", distance=" + distance +
            '}';
    }
}
