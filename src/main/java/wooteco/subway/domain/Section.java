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

    public boolean isSameSection(final Section newSection) {
        return isSameUpStation(newSection) && isSameDownStation(newSection);
    }

    public boolean isSameDownStation(final Section newSection) {
        return downStation.equals(newSection.downStation);
    }

    public boolean isSameUpStation(final Section newSection) {
        return upStation.equals(newSection.upStation);
    }

    public boolean isUpStationSameAsDownStation(final Section newSection) {
        return upStation.equals(newSection.downStation);
    }

    public boolean isDownStationSameAsUpStation(final Section newSection) {
        return downStation.equals(newSection.upStation);
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

    public DeleteMatchingResult matchStation(final Station target) {
        if (this.downStation.isSameName(target)) {
            return DeleteMatchingResult.POSSIBLE_TO_DELETE;
        }
        return DeleteMatchingResult.NO_MATCHED;
    }

    public DeleteMatchingResult matchWithLastUpStation(final Station target) {
        if (this.upStation.isSameName(target)) {
            return DeleteMatchingResult.POSSIBLE_TO_DELETE;
        }
        return DeleteMatchingResult.NO_MATCHED;
    }

    public DeleteMatchingResult matchWithLastDownStation(final Station taret) {
        if (this.downStation.isSameName(taret)) {
            return DeleteMatchingResult.POSSIBLE_TO_DELETE;
        }
        return DeleteMatchingResult.NO_MATCHED;
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
