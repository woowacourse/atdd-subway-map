package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private final Map<Long, Station> stations = new HashMap<>();

    @Override
    public Station save(Station station) {
        Long id = ++seq;
        stations.put(id, new Station(id, station.getName()));
        return stations.get(id);
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
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
    public boolean existsByName(Station station) {
        return stations.values()
                .stream()
                .anyMatch(it -> it.hasSameName(station));
    }
}
