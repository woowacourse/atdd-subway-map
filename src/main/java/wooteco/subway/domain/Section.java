package wooteco.subway.domain;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validate(distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    private void validate(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("거리는 1이상이어야 합니다.");
        }
    }

    public boolean hasUpSection(Section other) {
        return upStation.equals(other.upStation);
    }

    public boolean hasDownSection(Section other) {
        return downStation.equals(other.upStation);
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
}
