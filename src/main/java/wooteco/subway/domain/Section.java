package wooteco.subway.domain;

public class Section {

    private Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isBetweenDistance(int distance) {
        return this.distance > distance;
    }

    public boolean isSameStations(Long stationId1, Long stationId2) {
        if (upStation.getId().equals(stationId1) && downStation.getId().equals(stationId2)) {
            return true;
        }
        return downStation.getId().equals(stationId1) && upStation.getId().equals(stationId2);
    }
}
