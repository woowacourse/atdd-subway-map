package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

import java.util.List;

public class Section {
    private final Long id;
    private final List<Station> stations;
    private final Distance distance;

    public Section(List<Station> stations, Distance distance) {
        this(null, stations, distance);
    }

    public Section(Long id, List<Station> stations, Distance distance) {
        validateStations(stations);
        validateNotEmpty(distance);
        this.id = id;
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


}