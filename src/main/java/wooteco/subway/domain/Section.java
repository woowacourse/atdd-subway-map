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
        if (isSameSection(newSection)) {
            return MatchingResult.SAME_SECTION;
        }
        if (canAddToLeft(newSection)) {
            return MatchingResult.ADD_TO_LEFT;
        }
        if (canAddToRight(newSection)) {
            return MatchingResult.ADD_TO_RIGHT;
        }
        return MatchingResult.NO_MATCHED;
    }

    private boolean isSameSection(final Section newSection) {
        return upStation.equals(newSection.upStation)
                && downStation.equals(newSection.downStation);
    }

    private boolean canAddToLeft(final Section newSection) {
        return upStation.equals(newSection.downStation)
                ^ downStation.equals(newSection.downStation);
    }

    private boolean canAddToRight(final Section newSection) {
        return upStation.equals(newSection.upStation)
                ^ downStation.equals(newSection.upStation);
    }

    public boolean isDistanceLongerThan(final Section newSection) {
        return this.distance > newSection.distance;
    }
}
