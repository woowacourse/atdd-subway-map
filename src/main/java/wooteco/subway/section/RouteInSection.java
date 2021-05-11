package wooteco.subway.section;

import java.util.Objects;

public class RouteInSection {

    private long upStationId;
    private long downStationId;

    public RouteInSection(long upStationId, long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }


    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    @Override
    public String toString() {
        return "RouteInSection{" +
            "upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RouteInSection)) {
            return false;
        }
        RouteInSection that = (RouteInSection) o;
        return upStationId == that.upStationId &&
            downStationId == that.downStationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId);
    }

    public boolean isNotDownStationId(long targetStationId) {
        return this.downStationId != targetStationId;
    }
}
