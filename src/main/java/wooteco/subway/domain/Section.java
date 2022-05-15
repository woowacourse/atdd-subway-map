package wooteco.subway.domain;

import java.util.List;

public class Section {

    static final String DUPLICATE_STATION_EXCEPTION_MESSAGE = "상행역과 하행역은 같을 수 없습니다.";
    static final String STATION_DISTANCE_EXCEPTION_MESSAGE = "두 역간의 거리는 0보다 커야합니다.";

    private static final int MINIMUM_DISTANCE = 0;

    private Long id;
    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;

    public Section(Long id, long lineId, long upStationId, long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        validateDuplicateStation();
        validateDistance();
    }

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public void validateDuplicateStation() {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException(DUPLICATE_STATION_EXCEPTION_MESSAGE);
        }
    }

    public void validateDistance() {
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(STATION_DISTANCE_EXCEPTION_MESSAGE);
        }
    }

    public boolean canLinkWithUpStation(Section other) {
        return upStationId == other.downStationId;
    }

    public boolean canLinkWithDownStation(Section other) {
        return downStationId == other.upStationId;
    }

    public boolean isSameUpStation(Section other) {
        return other.upStationId == this.upStationId;
    }

    public boolean isSameDownStation(Section other) {
        return other.downStationId == this.downStationId;
    }

    public boolean isLessThanDistance(Section other) {
        return this.distance < other.distance;
    }

    public boolean hasUpStation(long stationId) {
        return upStationId == stationId;
    }

    public boolean hasDownStation(long stationId) {
        return downStationId == stationId;
    }

    public boolean hasStation(long stationId) {
        return upStationId == stationId || downStationId == stationId;
    }

    public Long getId() {
        return id;
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

    public List<Long> getStationIds() {
        return List.of(upStationId, downStationId);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
