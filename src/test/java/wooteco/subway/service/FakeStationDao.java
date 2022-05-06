package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private final List<Station> stations = new ArrayList<>();

    @Override
    public Optional<Station> save(final Station station) {
        if (isDuplicateName(station)) {
            return Optional.empty();
        }

        final Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return Optional.of(persistStation);
    }

    private boolean isDuplicateName(final Station station) {
        return stations.stream()
                .anyMatch(it -> it.isSameName(station));
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Integer deleteById(final Long id) {
        final Station foundStation = stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        stations.remove(foundStation);
        return 1;
    }

    private Station createNewObject(final Station station) {
        final Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}