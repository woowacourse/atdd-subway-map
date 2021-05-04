package wooteco.subway.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicateException;

public class StationDao {

    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        validateDuplicateName(station);
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    private void validateDuplicateName(Station station) {
        if (stations.contains(station)) {
            throw new DuplicateException();
        }
    }

    public void deleteAll() {
        stations.clear();
        seq = 0L;
    }
}
