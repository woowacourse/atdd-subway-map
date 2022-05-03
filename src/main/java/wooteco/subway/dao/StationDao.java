package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.exception.NotFoundStationException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private Station findById(Long id) {
        return stations.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundStationException("id에 맞는 지하철역이 없습니다."));
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

    public void deleteById(Long id) {
        Station station = findById(id);
        stations.remove(station);
    }

    public void clear() {
        stations.clear();
    }
}
