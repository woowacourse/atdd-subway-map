package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;

import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private final Map<Long, Station> stations = new HashMap<>();

    @Override
    public Station save(Station station) {
        validateDuplicateName(station);
        Station persistStation = createStation(station);
        stations.put(seq, persistStation);
        return persistStation;
    }

    private void validateDuplicateName(Station station) {
        if (stations.containsValue(station)) {
            throw new DuplicateKeyException("이미 존재하는 데이터 입니다.");
        }
    }

    @Override
    public Optional<Station> findById(Long id) {
        return Optional.of(stations.get(id));
    }

    private Station createStation(Station station) {
        return new Station(++seq, station.getName());
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return stations.remove(id) != null;
    }
}
