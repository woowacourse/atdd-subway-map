package wooteco.subway.line;

import java.util.Objects;

public class LineEndPoint {

    private long upStationId;
    private long downStationId;

    public LineEndPoint(long upStationId, long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }


    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public boolean isNotDownStationId(long targetStationId) {
        return this.downStationId != targetStationId;
    }

    public boolean isSameUpStationId(long upStationId) {
        return this.upStationId == upStationId;
    }

    public boolean isSameDownStationId(long upStationId) {
        return this.downStationId == upStationId;
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
        if (!(o instanceof LineEndPoint)) {
            return false;
        }
        LineEndPoint that = (LineEndPoint) o;
        return upStationId == that.upStationId &&
            downStationId == that.downStationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId);
    }
}
