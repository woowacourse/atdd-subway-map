package wooteco.subway.domain;

public class Section {
    private final long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(String upStation, String downStation, int distance) {
        this(0L, new Station(upStation), new Station(downStation), distance);
    }

    public Section(long id, String upStation, String downStation, int distance) {
        this(id, new Station(upStation), new Station(downStation), distance);
    }

    public Section(long id, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation, distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(Station upStation, Station downStation, int distance) {
        checkWhetherStationsAreDifferent(upStation, downStation);
        checkValidDistance(distance);
    }

    private void checkWhetherStationsAreDifferent(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행 종점과 하행 종점이 같을 수 없습니다.");
        }
    }

    private void checkValidDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("두 종점간의 거리는 0보다 커야합니다.");
        }
    }
}
