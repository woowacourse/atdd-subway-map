package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private Map<Long, Station> stations = new HashMap<>();

    @Override
    public Station save(Station station) {
        Long id = ++seq;
        stations.put(id, new Station(id, station.getName()));
        return stations.get(id);
    }

    @Override
    public List<Station> findAll() {
        return stations.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public int deleteById(Long id) {
        if (stations.containsKey(id)) {
            stations.remove(id);
            return 1;
        }
        return 0;
    }

    @Override
    public boolean exists(Station station) {
        return stations.values()
                .stream()
                .anyMatch(it -> it.getName().equals(station.getName()));
    }

    @Override
    public boolean exists(final Long id) {
        return stations.containsKey(id);
    }
}
