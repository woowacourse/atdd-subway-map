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

    public Section changeDownStationAndDistance(final Section newSection, final Station newStation) {
        return new Section(this.upStation, newStation, newSection.distance);
    }

    public Section changeUpStationAndDistance(final Section newSection, final Station newStation) {
        return new Section(newStation, this.downStation, distance - newSection.distance);
    }

    public Section combineTwoSection(final Section section) {
        return new Section(upStation, section.downStation, distance + section.distance);
    }

    public MatchingResult matchStation(final Station target) {
        if (this.downStation.isSameName(target)) {
            return MatchingResult.POSSIBLE_TO_DELETE;
        }
        return MatchingResult.NO_MATCHED;
    }

    public MatchingResult matchWithLastUpStation(final Station target) {
        if (this.upStation.isSameName(target)) {
            return MatchingResult.POSSIBLE_TO_DELETE;
        }
        return MatchingResult.NO_MATCHED;
    }

    public MatchingResult matchWithLastDownStation(final Station taret) {
        if (this.downStation.isSameName(taret)) {
            return MatchingResult.POSSIBLE_TO_DELETE;
        }
        return MatchingResult.NO_MATCHED;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
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

    @Override
    public String toString() {
        return "Section{" +
                "upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
