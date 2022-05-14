package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        boolean existName = stations.stream()
                .anyMatch(it -> isSameName(it, station));
        if (existName) {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmptyResultDataAccessException(1));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public List<Station> findByIdIn(List<Long> ids) {
        return stations;
    }

    @Override
    public void deleteById(Long id) {
        if (existsById(id)) {
            stations.remove(findById(id));
        }
    }

    private boolean isSameName(Station station1, Station station2) {
        return station1.getName().equals(station2.getName());
    }

    private boolean existsById(Long id) {
        return stations.stream().anyMatch(station -> station.getId().equals(id));
    }
}
