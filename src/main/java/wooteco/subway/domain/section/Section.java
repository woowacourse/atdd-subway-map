package wooteco.subway.domain.section;

import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.station.Station;

public class Section {

    private static final String SAME_STATION_INPUT_EXCEPTION = "서로 다른 두 개의 역을 입력해야 합니다.";
    private static final String INVALID_DISTANCE_EXCEPTION = "구간의 길이는 1이상이어야 합니다.";
    private static final String NON_ADJACENT_SECTIONS_EXCEPTION = "서로 겹치는 구간이 아닙니다.";
    private static final int MIN_DISTANCE = 1;

    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        validateDifferentStations(upStation, downStation);
        validateDistance(distance);
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    private void validateDifferentStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_INPUT_EXCEPTION);
        }
    }

    public List<Station> toStations() {
        return List.of(upStation, downStation);
    }

    public boolean hasUpStationOf(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStationOf(Station station) {
        return downStation.equals(station);
    }

    public boolean hasStationOf(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean isRegisteredAtLine(Long lineId) {
        return lineId.equals(this.lineId);
    }

    public int toConnectedDistance(Section adjacentSection) {
        validateAdjacentSections(adjacentSection);
        return distance + adjacentSection.distance;
    }

    public int toRemainderDistance(Section coveringSection) {
        validateAdjacentSections(coveringSection);
        int remainderDistance = distance - coveringSection.distance;
        validateDistance(remainderDistance);
        return remainderDistance;
    }

    private void validateAdjacentSections(Section target) {
        boolean isAdjacent = hasStationOf(target.upStation)
                || hasStationOf(target.downStation);
        if (!isAdjacent) {
            throw new IllegalArgumentException(NON_ADJACENT_SECTIONS_EXCEPTION);
        }
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(INVALID_DISTANCE_EXCEPTION);
        }
    }

    public Long getLineId() {
        return lineId;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance
                && Objects.equals(lineId, section.lineId)
                && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
