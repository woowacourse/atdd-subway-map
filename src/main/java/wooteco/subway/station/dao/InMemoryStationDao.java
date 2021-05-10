package wooteco.subway.station.dao;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryStationDao implements StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Optional<Station> findStationById(Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny();
    }

    @Override
    public Optional<Station> findStationByName(String name) {
        return stations.stream()
                .filter(station -> station.isSameName(name))
                .findAny();
    }

    @Override
    public void delete(Long id) {
        stations.removeIf(station -> station.isSameId(id));
    }
}
