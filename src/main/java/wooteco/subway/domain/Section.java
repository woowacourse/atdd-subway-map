package wooteco.subway.domain;

public class Section {

    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final Long lineOrder;

    public Section(Line line, Station upStation, Station downStation, int distance, Long lineOrder) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.lineOrder = lineOrder;
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

    public Long getLineOrder() {
        return lineOrder;
    }
}
