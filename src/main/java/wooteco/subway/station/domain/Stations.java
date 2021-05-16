package wooteco.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public Stations sortStationsByIds(List<Long> stationsIds) {
        List<Station> sortContainer = new ArrayList<>();
        stationsIds.forEach(stationsId -> sortContainer.add(findById(stationsId)));
        return new Stations(sortContainer);
    }

    private Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역 ID 입니다."));
    }

    public Stream<Station> stream() {
        return stations.stream();
    }

    public List<Station> toList() {
        return new ArrayList<>(stations);
    }
}
