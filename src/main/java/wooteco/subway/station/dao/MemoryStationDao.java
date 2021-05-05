package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.station.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryStationDao implements StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        if(validateDuplicateName(station)) {
           throw new DuplicatedNameException();
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return Collections.unmodifiableList(stations);
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void delete(Long id) {
        Station findByIdStation = stations.stream()
                .filter(station -> station.equalId(id))
                .findFirst()
                .get();
        stations.remove(findByIdStation);
    }

    private boolean validateDuplicateName(Station newStation) {
        return stations.stream()
                .anyMatch(station -> station.equalName(newStation));
    }
}
