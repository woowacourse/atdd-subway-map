package wooteco.subway.section;

import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.line.LineRequest;

public class Section {
    private final long upStationId;
    private final long downStationId;
    private final int distance;
    private long lineId;

    public Section(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = validateDistance(distance);
    }

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
    }

    private int validateDistance(int distance) {
        if(distance < 0) {
            throw new IllegalInputException();
        }
        return distance;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
