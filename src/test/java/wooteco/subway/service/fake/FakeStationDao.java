package wooteco.subway.service.fake;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private static final int DELETE_SUCCESS = 1;

    private static Long seq = 0L;
    private final List<Station> stations = new ArrayList<>();

    @Override
    public Long save(Station station) {
        if (stations.contains(station)) {
            throw new DuplicateKeyException("동일한 station이 존재합니다.");
        }

        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation.getId();
    }

    @Override
    public Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Station> findAll() {
        return List.copyOf(stations);
    }

    @Override
    public int deleteById(Long id) {
        final Station findStation = stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);

        stations.remove(findStation);
        return DELETE_SUCCESS;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
