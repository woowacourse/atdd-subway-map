package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class MockStationDao implements StationDao {

    private final Map<Long, Station> mockDb = new HashMap<>();
    private long sequenceId = 1;


    @Override
    public Long save(Station station) {
        Long id = sequenceId;
        mockDb.put(sequenceId++, new Station(id, station.getName()));
        return id;
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(mockDb.values());
    }

    @Override
    public Station findById(Long id) {
        return mockDb.get(id);
    }

    @Override
    public void deleteById(Long id) {
        mockDb.remove(id);
    }

    @Override
    public boolean existById(Long id) {
        return mockDb.containsKey(id);
    }
}
