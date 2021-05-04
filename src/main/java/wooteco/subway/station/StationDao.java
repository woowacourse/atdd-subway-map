package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
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
        stations.remove(findById(id));
    }

    public Station findById(Long id) {
        return stations.stream()
                       .filter(station -> station.getId()
                                                 .equals(id))
                       .findFirst()
                       .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }
}
