package wooteco.subway.admin.domain;

public class LineStation {
    private Long line;
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public LineStation() {
    }

    public LineStation(final Long line, final Long preStationId, final Long stationId,
                       final int distance, final int duration) {
        this.line = line;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getLine() {
        return line;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public void updatePreLineStation(Long preStationId) {
        this.preStationId = preStationId;
    }

    public String getCustomId() {
        return "" + getLine() + getPreStationId() + getStationId();
    }

    @Override
    public String toString() {
        return "LineStation{" +
                "line=" + line +
                ", preStationId=" + preStationId +
                ", stationId=" + stationId +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }
}
