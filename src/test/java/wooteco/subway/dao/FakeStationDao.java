package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private static final int EXECUTED_COLUMN_COUNT_ONE = 1;
    private static final int EXECUTED_COLUMN_COUNT_NONE = 0;

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
            return EXECUTED_COLUMN_COUNT_ONE;
        }
        return EXECUTED_COLUMN_COUNT_NONE;
    }

    @Override
    public boolean existsByName(Station station) {
        return stations.values()
                .stream()
                .anyMatch(it -> it.hasSameName(station));
    }

    @Override
    public List<Station> findStationByIds(List<Long> ids) {
        List<Station> targets = new ArrayList<>();
        for (Entry<Long, Station> entry : stations.entrySet()) {
            if (ids.contains(entry.getKey())) {
                targets.add(entry.getValue());
            }
        }
        return targets;
    }
}
