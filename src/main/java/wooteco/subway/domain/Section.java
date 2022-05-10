package wooteco.subway.domain;

import java.util.Objects;

public class Section {
    private final Long lindId;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public Section(final Long lindId, final Long upStationId, final Long downStationId, final Integer distance) {
        this.lindId = lindId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLindId() {
        return lindId;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(lindId, section.lindId) && Objects
                .equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lindId, upStationId, downStationId, distance);
    }
}
