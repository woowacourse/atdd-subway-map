package wooteco.subway.domain;

import java.util.Objects;

public class StationInfo {
    private final Long linkedStationId;
    private final Long distance;

    public StationInfo(Long linkedStationId, Long distance) {
        this.linkedStationId = linkedStationId;
        this.distance = distance;
    }

    public boolean isBlankLink() {
        return linkedStationId < 0;
    }

    public StationInfo copyInfo() {
        return new StationInfo(this.linkedStationId, this.distance);
    }

    public Long getLinkedStationId() {
        return linkedStationId;
    }

    public Long getDistance() {
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
        StationInfo that = (StationInfo) o;
        return Objects.equals(linkedStationId, that.linkedStationId) && Objects.equals(distance,
                that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkedStationId, distance);
    }
}
