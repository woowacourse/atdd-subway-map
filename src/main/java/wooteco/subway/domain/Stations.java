package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.exception.ExceptionMessage;

public class Stations {

    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> sortBy(List<Long> ids) {
        return ids.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    private Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.STATION_ID_NOT_FOUND.getContent()));
    }
}
