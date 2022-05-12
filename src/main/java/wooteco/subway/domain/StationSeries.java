package wooteco.subway.domain;

import java.util.List;

import wooteco.subway.exception.RowDuplicatedException;

public class StationSeries {

    private final List<Station> stations;

    public StationSeries(List<Station> stations) {
        this.stations = stations;
    }

    public Station create(String name) {
        validateDistinct(name);
        return new Station(name);
    }

    private void validateDistinct(String name) {
        if (stations.stream().anyMatch(station -> station.getName().equals(name))) {
            throw new RowDuplicatedException(String.format("%s는 이미 존재하는 역 이름입니다.", name));
        }
    }
}
