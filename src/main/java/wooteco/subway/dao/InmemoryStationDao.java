package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class InmemoryStationDao implements StationDao {

    private static InmemoryStationDao INSTANCE;

    private Long seq = 0L;
    private final Map<Long, Station> stations = new HashMap<>();

    private InmemoryStationDao() {
    }

    public static synchronized InmemoryStationDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InmemoryStationDao();
        }
        return INSTANCE;
    }

    public void clear() {
        stations.clear();
    }

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(persistStation.getId(), persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public boolean existByName(final String name) {
        return stations.values()
                .stream()
                .anyMatch(station -> station.isSameName(name));
    }

    @Override
    public boolean existById(Long id) {
        return stations.containsKey(id);
    }

    @Override
    public void delete(final Long stationId) {
        Station removedStation = stations.remove(stationId);
        if (removedStation == null) {
            throw new IllegalArgumentException("없는 station 입니다.");
        }
    }
}
