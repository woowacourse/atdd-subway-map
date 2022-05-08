package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.domain.exception.UnmergeableException;
import wooteco.subway.domain.exception.UnsplittableException;

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
        if (isSameStationIds(edge) || isGreaterAndEqualDistance(edge.distance)) {
            throw new UnsplittableException(this, edge);
        }

        if (isSameWithUpStationId(edge.upStationId)) {
            return new SectionEdge(edge.downStationId, downStationId, distance - edge.distance);
        }

        if (isSameWithDownStationId(edge.downStationId)) {
            return new SectionEdge(upStationId, edge.upStationId, distance - edge.distance);
        }

        throw new UnsplittableException(this, edge);
    }

    private boolean isSameStationIds(SectionEdge e) {
        return (isSameWithUpStationId(e.upStationId) && isSameWithDownStationId(e.downStationId))
            || (isSameWithUpStationId(e.downStationId) && isSameWithDownStationId(e.upStationId));
    }

    private boolean isSameWithUpStationId(Long upStationId) {
        return this.upStationId.equals(upStationId);
    }

    private boolean isSameWithDownStationId(Long downStationId) {
        return this.downStationId.equals(downStationId);
    }

    private boolean isGreaterAndEqualDistance(int distance) {
        return this.distance <= distance;
    }

    public SectionEdge merge(SectionEdge edge) {
        if (isSameStationIds(edge)) {
            throw new UnmergeableException(this, edge);
        }

        if (isSameWithUpStationId(edge.downStationId)) {
            return new SectionEdge(edge.upStationId, downStationId, distance + edge.distance);
        }

        if (isSameWithDownStationId(edge.upStationId)) {
            return new SectionEdge(upStationId, edge.downStationId, distance + edge.distance);
        }

        throw new UnmergeableException(this, edge);
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
