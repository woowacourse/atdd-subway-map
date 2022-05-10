package wooteco.subway.domain;

import java.util.Objects;

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
        if (this.downStation.equals(newSection.downStation)) {
            return MatchingResult.ADD_TO_LEFT;
        }
        if (this.upStation.equals(newSection.upStation)) {
            return MatchingResult.ADD_TO_RIGHT;
        }
        return MatchingResult.NO_MATCHED;
    }

    public MatchingResult matchStartStation(final Section newSection) {
        if (this.upStation.equals(newSection.downStation)) {
            return MatchingResult.ADD_TO_LEFT;
        }
        return MatchingResult.NO_MATCHED;
    }

    public MatchingResult matchEndStation(final Section newSection) {
        if (this.downStation.equals(newSection.upStation)) {
            return MatchingResult.ADD_TO_RIGHT;
        }
        return MatchingResult.NO_MATCHED;
    }

    private boolean isSameSection(final Section newSection) {
        return upStation.equals(newSection.upStation)
                && downStation.equals(newSection.downStation);
    }

    public boolean isDistanceLongerThan(final Section newSection) {
        return this.distance > newSection.distance;
    }

    public Station getNewStation(final MatchingResult result) {
        if (result == MatchingResult.ADD_TO_LEFT) {
            return upStation;
        }
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Section changeDownStationAndDistance(final Section newSection, final Station newStation) {
        return new Section(this.upStation, newStation, newSection.distance);
    }

    public Section changeUpStationAndDistance(final Section newSection, final Station newStation) {
        return new Section(newStation, this.downStation, distance - newSection.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }
}
