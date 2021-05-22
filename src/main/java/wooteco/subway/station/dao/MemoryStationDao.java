package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.station.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MemoryStationDao implements StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        if (validateDuplicateName(station)) {
            throw new DuplicatedNameException();
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return Collections.unmodifiableList(stations);
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.equalId(id))
                .findAny();
    }

    @Override
    public void delete(Long id) {
        stations.removeIf(station -> station.equalId(id));
    }

    @Override
    public Optional<Station> findStationByName(String name) {
        return stations.stream()
                .filter(station -> station.equalName(name))
                .findAny();
    }

    @Override
    public List<Station> findByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    private boolean validateDuplicateName(Station newStation) {
        return stations.stream()
                .anyMatch(station -> station.equalName(newStation.getName()));
    }
}
