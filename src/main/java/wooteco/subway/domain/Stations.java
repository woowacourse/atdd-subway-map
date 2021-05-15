package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.NotFoundException;

public class Stations {


    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public Station findById(Long id) {
        return stations.stream()
            .filter(station -> station.matchId(id))
            .findAny()
            .orElseThrow(NotFoundException::new);
    }
}
