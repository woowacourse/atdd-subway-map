package wooteco.subway.service.fakeDao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationDaoImpl implements StationDao {
    private static StationDaoImpl stationDao = new StationDaoImpl();

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static StationDaoImpl getInstance() {
        return stationDao;
    }

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        if (hasStation(persistStation.getName())) {
            throw new IllegalArgumentException("같은 이름의 역이 존재합니다.");
        }
        stations.add(persistStation);
        return new Station(persistStation.getId(), station.getName());
    }

    @Override
    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 역이 존재하지 않습니다."));
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public void deleteById(Long id) {
        boolean result = stations.removeIf(station -> station.getId() == id);
        if (!result) {
            throw new NoSuchElementException("해당하는 역이 존재하지 않습니다.");
        }
    }

    @Override
    public boolean hasStation(String name) {
        return stations.stream()
                .anyMatch(station -> name.equals(station.getName()));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
