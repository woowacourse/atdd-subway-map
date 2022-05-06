package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id, final Line line, final Station upStation, final Station downStation, final int distance) {
        validatePositiveDistance(distance);
        validateDuplicateStation(upStation, downStation);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validatePositiveDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이는 양수만 들어올 수 있습니다.");
        }
    }

    private void validateDuplicateStation(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("upstation과 downstation은 중복될 수 없습니다.");
        }
    }

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(final Long id, final Section section) {
        this(id, section.line, section.upStation, section.downStation, section.distance);
    }

    public boolean isSameUpStationAndDownStation(final Station upStation, final Station downStation) {
        return (this.upStation.equals(upStation) && this.downStation.equals(downStation)) ||
                (this.upStation.equals(downStation) && this.downStation.equals(upStation));
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
