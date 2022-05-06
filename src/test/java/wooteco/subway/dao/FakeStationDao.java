package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    @Override
    public Station insert(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Boolean existByName(Station station) {
        return stations.stream()
                .anyMatch(it -> it.getName().equals(station.getName()));
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public void delete(Long id) {
        Station foundStation = stations.stream()
                .filter(station -> station.getId() == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 존재하지 않습니다."));
        stations.remove(foundStation);
    }

    public void clear() {
        stations.clear();
    }
}
