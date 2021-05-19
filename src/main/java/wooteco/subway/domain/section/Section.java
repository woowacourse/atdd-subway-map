package wooteco.subway.domain.section;

import java.util.Objects;
import wooteco.subway.domain.station.Station;

public class Section {

    private final Long id;
    private final Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this (null, null, upStation, downStation, distance);
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        validateStation(upStation, downStation);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("지하철 구간의 거리는 0 혹은 음수가 될 수 없습니다.");
        }
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation == null || downStation == null) {
            throw new IllegalArgumentException("상행역과 하행역은 null이 될 수 없습니다.");
        }
    }

    public boolean exists(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public Section splitedAndUpdate(Section requestSection) {
        validateSplitAndUpdate(requestSection);
        int updatedDistance = this.distance - requestSection.getDistance();
        if (this.upStation.equals(requestSection.upStation)) {
            return new Section(id, lineId, requestSection.getDownStation(), this.downStation, updatedDistance);
        }
        return new Section(id, lineId, this.upStation, requestSection.getUpStation(), updatedDistance);
    }

    public Section mergeAndUpdate(Section requestSection) {
        int updatedDistance = this.distance + requestSection.distance;
        return new Section(id, lineId, this.upStation, requestSection.downStation, updatedDistance);
    }

    private void validateSplitAndUpdate(Section requestSection) {
        if (this.distance <= requestSection.distance) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    public boolean isConnectedBetweenDownAndUp(Section section) {
        return downStation.equals(section.upStation);
    }

    public Long getId() {
        return id;
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
