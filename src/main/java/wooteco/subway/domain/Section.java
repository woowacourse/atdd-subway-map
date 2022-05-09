package wooteco.subway.domain;

public class Section {

    private Station upStation;
    private Station downStation;

    public Section(Station upStation, Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
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
}
