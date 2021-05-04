package wooteco.subway.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;

public class StationDaoCache implements StationDao {

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
    public Optional<Station> findStationByName(String name) {
        return stations.stream()
            .filter(station -> station.isSameName(name))
            .findAny();
    }
}
