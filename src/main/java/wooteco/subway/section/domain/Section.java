package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Objects;

public class Section {
    private final Long id;
    private final Long lineId;
    private final List<Station> stations;
    private final Distance distance;

    public Section(List<Station> stations, Distance distance) {
        this(null, null, stations, distance);
    }

    public Section(Long lineId, List<Station> stations, Distance distance) {
        this(null, lineId, stations, distance);
    }
    public Section(Long id, Long lineId, List<Station> stations, Distance distance) {
        validateStations(stations);
        validateNotEmpty(distance);
        this.id = id;
        this.lineId = lineId;
        this.stations = stations;
        this.distance = distance;
    }

    private void validateNotEmpty(Distance distance) {
        if (distance == null) {
            throw new IllegalArgumentException("거리를 입력해주세요.");
        }
    }
    private void validateStations(List<Station> stations) {
        if (stations.size() != 2) {
            throw new IllegalArgumentException("역을 모두 입력해주세요.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public List<Station> getStations() {
        return stations;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}