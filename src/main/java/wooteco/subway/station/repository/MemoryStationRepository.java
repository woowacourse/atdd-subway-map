package wooteco.subway.station.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.station.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryStationRepository implements StationRepository {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

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
    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findFirst()
                .get();
    }

    @Override
    public void delete(Long id) {
        Station findByIdStation = findById(id);
        stations.remove(findByIdStation);
    }

    private boolean validateDuplicateName(Station newStation) {
        return stations.stream()
                .anyMatch(station -> station.isSameName(newStation));
    }
}
