package wooteco.subway.section;

import wooteco.subway.line.SectionRequest;

public class Section {

    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, SectionRequest sectionRequest) {
        this(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
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

    public boolean isNotDownStation(Long stationId) {
        return stationId != downStationId;
    }
}
