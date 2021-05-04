package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.StationDuplicationException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static StationDao instance;
    private Long seq = 0L;
    private final List<Station> stations = new ArrayList<>();

    private StationDao() {
    }

    public static StationDao getInstance() {
        if (instance == null) {
            instance = new StationDao();
        }

        return instance;
    }

    public Station save(Station station) {
        validateDuplicatedStation(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private void validateDuplicatedStation(Station newStation) {
        if (isDuplicated(newStation)) {
            throw new StationDuplicationException();
        }
    }

    private boolean isDuplicated(Station newStation) {
        return stations.stream()
            .anyMatch(station -> station.isSameName(newStation));
    }

    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void delete(Long id) {
        stations.stream()
            .filter(station -> station.isSameId(id))
            .findAny()
            .ifPresent(station -> stations.remove(station));
    }
}
