package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.domain.Station;

public class FakeStationDao extends StationDao {

    private Long seq = 0L;
    private final Map<Long, Station> stations = new HashMap<>();

    public FakeStationDao() {
        super(Mockito.mock(DataSource.class));
    }

    @Override
    public Station save(Station station) {
        validateUnique(station);
        Station persistStation = new Station(++seq, station.getName());
        stations.put(seq, persistStation);
        return persistStation;
    }

    private void validateUnique(Station station) {
        if (stations.values()
                .stream()
                .anyMatch(savedStation -> savedStation.getName().equals(station.getName()))) {
            throw new DuplicateKeyException("");
        }
    }

    @Override
    public List<Station> findAll() {
        return List.copyOf(stations.values());
    }

    @Override
    public Station findById(Long id) {
        validateExist(id);
        return stations.get(id);
    }

    private void validateExist(Long id) {
        if (!stations.containsKey(id)) {
            throw new EmptyResultDataAccessException(1);
        }
    }

    @Override
    public void deleteById(Long id) {
        validateExist(id);
        stations.remove(id);
    }

}
