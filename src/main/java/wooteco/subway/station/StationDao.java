package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.exception.NoSuchStationException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao implements StationRepository {
    private Long seq = 0L;
    private final List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        this.stations.add(persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Station findById(long id) {
        return this.stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchStationException("해당하는 id의 역이 없습니다."));
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Optional<Station> findByName(String name) {
        return this.stations.stream()
                .filter(station -> station.getName().equals(name))
                .findAny();
    }

    @Override
    public void delete(Long id) {
        if (!stations.removeIf(station -> station.getId().equals(id))) {
            throw new NoSuchStationException("해당하는 id의 역이 없습니다.");
        }
    }
}
