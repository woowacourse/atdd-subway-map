package wooteco.subway.domain;

public class Section {

    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public MatchingResult match(final Section newSection) {
        if (upStation.equals(newSection.upStation) && downStation.equals(newSection.downStation)) {
            return MatchingResult.SAME_SECTION;
        }
        if (upStation.equals(newSection.upStation)) {
            return MatchingResult.SAME_UP_STATION;
        }
        if (downStation.equals(newSection.downStation)) {
            return MatchingResult.SAME_DOWN_STATION;
        }
        return MatchingResult.NO_MATCHED;
    }

    public boolean isDistanceLongerThan(final Section newSection) {
        return this.distance > newSection.distance;
    }
}
