package wooteco.subway.domain;


//의존성 방향: Line->Section->Station
public class Section {
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
}
