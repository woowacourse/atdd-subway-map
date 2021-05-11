package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(0L, 0L, new Station(upStationId), new Station(downStationId), distance);
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(0L, 0L, upStation, downStation, distance);
    }

    public Section(final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this(0L, lineId, upStation, downStation, distance);
    }

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        this(id, lineId, new Station(upStationId), new Station(downStationId), distance);
    }

    public Section(final Long id, final Long lineId, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long id() {
        return id;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public int distance() {
        return distance;
    }

    public Long lineId() {
        return lineId;
    }


    public boolean has(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean hasUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean lessDistanceThan(Section anotherSection) {
        return this.distance <= anotherSection.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) &&
                Objects.equals(lineId, section.lineId) &&
                Objects.equals(upStation, section.upStation) &&
                Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStation, downStation);
    }
}
