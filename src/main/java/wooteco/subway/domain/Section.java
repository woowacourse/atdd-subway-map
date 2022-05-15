package wooteco.subway.domain;

import java.util.Objects;

public class Section {
    private static final String ERROR_MESSAGE_SAME_UP_AND_DOWN_STATION = "구간의 상행역과 하행역은 달라야합니다.";
    private static final String ERROR_MESSAGE_DISTANCE_MUST_PLUS = "구간 사이의 거리는 양수여야합니다.";
    private static final String ERROR_MESSAGE_NOT_CONNECT_CONDITION = "연결 조건이 맞지 않습니다.";
    private static final int MINIMUM_DISTANCE_VALUE = 1;

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Station upStation, Station downStation, int distance) {
        validateUpAndDownAreDifferent(upStation, downStation);
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateUpAndDownAreDifferent(upStation, downStation);
        validateDistance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateUpAndDownAreDifferent(Station up, Station down) {
        if (up.equals(down)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_SAME_UP_AND_DOWN_STATION);
        }
    }

    private void validateDistance(Integer distance) {
        if (distance < MINIMUM_DISTANCE_VALUE) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DISTANCE_MUST_PLUS);
        }
    }

    public boolean canConnect(Section section) {
        return section.downStation.equals(this.upStation)
            || section.upStation.equals(this.downStation)
            || isSameUpStation(section)
            || isSameDownStation(section);
    }

    public boolean isSameUpStation(Section section) {
        return section.upStation.equals(this.upStation);
    }

    public boolean isSameUpStation(Station station) {
        return station.equals(this.upStation);
    }

    public boolean isSameDownStation(Section section) {
        return section.downStation.equals(this.downStation);
    }

    public boolean isSameDownStation(Station station) {
        return station.equals(this.downStation);
    }

    public boolean canInsert(Section section) {
        return section.distance < this.distance;
    }

    public void changeStationAndDistance(Section section) {
        if (this.upStation.equals(section.upStation)) {
            this.upStation = section.downStation;
            this.distance -= section.distance;
            return;
        }

        this.downStation = section.upStation;
        this.distance -= section.distance;
    }

    public void concatenate(Section section) {
        validateCanConcatenate(section);

        this.downStation = section.downStation;
        this.distance += section.distance;
    }

    private void validateCanConcatenate(Section section) {
        if (!this.downStation.equals(section.upStation)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_CONNECT_CONDITION);
        }
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Section))
            return false;
        Section section = (Section)o;
        return Objects.equals(getId(), section.getId()) && Objects.equals(getUpStation(),
            section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation())
            && getDistance() == section.getDistance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUpStation(), getDownStation(), getDistance());
    }
}
