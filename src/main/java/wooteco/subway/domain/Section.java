package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private static final int DISTANCE_MINIMUM = 0;

    private final Long id;
    private final Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        validateSameSection(upStation, downStation);
        validateDistance(distance);
        this.id = id;
        this.lineId = Objects.requireNonNull(lineId, "값을 입력해주세요" + "lineId");
        this.upStation = Objects.requireNonNull(upStation, "값을 입력해주세요" + "upStation");
        this.downStation = Objects.requireNonNull(downStation, "값을 입력해주세요" + "downStation");
        this.distance = distance;
    }

    private void validateSameSection(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 동일할 경우 구간에 등록할 수 없습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance <= DISTANCE_MINIMUM) {
            throw new IllegalArgumentException("구간의 거리는 0이하로 등록할 수 없습니다.");
        }
    }

    public boolean hasStation(Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();

        return isSameUpStation(upStation)
                || isSameUpStation(downStation)
                || isSameDownStation(upStation)
                || isSameDownStation(downStation);
    }

    public boolean isSameStation(Section section) {
        return isSameUpStation(section.getUpStation()) && isSameDownStation(section.getDownStation());
    }

    public boolean isUpdate(Section section) {
        return equals(section) && !isSameUpAndDownStation(section);
    }

    private boolean isSameUpAndDownStation(Section section) {
        return isSameUpStation(section.getUpStation())
                && isSameDownStation(section.getDownStation());
    }

    public boolean isSameUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isSameDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public boolean isOverDistance(Section section) {
        return this.distance < section.distance;
    }

    public void updateStation(Station upStation, Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public void splitDistance(int distance) {
        this.distance -= distance;
    }

    public Section merge(Section section) {
        final int sumDistance = this.distance + section.distance;

        if (isSameDownStation(section.upStation)) {
            return new Section(lineId, upStation, section.downStation, sumDistance);
        }
        return new Section(lineId, section.downStation, upStation, sumDistance);
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
        if (!(o instanceof Section)) {
            return false;
        }
        Section sectionV2 = (Section) o;
        return Objects.equals(id, sectionV2.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
