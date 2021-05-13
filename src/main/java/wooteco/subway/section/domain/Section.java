package wooteco.subway.section.domain;

import wooteco.subway.section.exception.SectionHasSameStationsException;
import wooteco.subway.section.exception.SectionNotSequentialException;
import wooteco.subway.station.domain.Station;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final SectionDistance distance;

    public Section(Station upStation, Station downStation, long distance) {
        this(null, upStation, downStation, new SectionDistance(distance));
    }

    public Section(Long id, Station upStation, Station downStation, long distance) {
        this(id, upStation, downStation, new SectionDistance(distance));
    }

    public Section(Long id, Section section) {
        this(id, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public Section(Long id, Station upStation, Station downStation, SectionDistance distance) {
        checkSameStations(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void checkSameStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new SectionHasSameStationsException(String.format("한 개의 역으로 이루어진 노선은 생성할 수 없습니다. 역 이름 : %s", upStation));
        }
    }

    public boolean isExist(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public Section updateUpStation(Station upStation, long distance) {
        return new Section(id, upStation, downStation, this.distance.minus(new SectionDistance(distance)));
    }

    public Section updateDownStation(Station downStation, long distance) {
        return new Section(id, upStation, downStation, this.distance.minus(new SectionDistance(distance)));
    }

    public Section mergeWithSequentialSection(Section that) {
        if (isNotSequential(that)) {
            throw new SectionNotSequentialException(
                    String.format(
                            "이어진 구간이 아닙니다. 구간1 : %s, 구간2 : %s",
                            this.toString(),
                            that.toString()
                    )
            );
        }

        return createSectionWithCommonStation(that);
    }

    private boolean isNotSequential(Section that) {
        return !this.downStation.equals(that.upStation)
                && !this.upStation.equals(that.downStation);
    }

    private Section createSectionWithCommonStation(Section that) {
        if (this.upStation.equals(that.getDownStation())) {
            return new Section(that.getId(), that.upStation, this.downStation, this.distance.sum(that.distance));
        }
        return new Section(this.getId(), this.upStation, that.downStation, this.distance.sum(that.distance));
    }

    public Station getUpStation() {
        return upStation;
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public long getDistance() {
        return distance.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "구간[id:" + this.id + upStation.toString() + "-" + downStation.toString() + ", 거리" + distance + "]";
    }
}
