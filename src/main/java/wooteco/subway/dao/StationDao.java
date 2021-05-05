package wooteco.subway.dao;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.domain.station.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public Optional<Station> findById(long id) {
        return stations.stream()
            .filter(station -> station.getId() == id)
            .findAny();
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void deleteById(long id) {
        stations.removeIf(station -> station.getId() == id);
    }
}
