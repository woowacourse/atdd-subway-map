package wooteco.subway.station;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station insert(String name) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO station (name) VALUES (?)";
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement pstmt = con.prepareStatement(
                    query,
                    new String[]{"id"});
            pstmt.setString(1, name);
            return pstmt;
        }, keyHolder);

        final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Station(id, name);
    }

    public static List<Station> findAll() {
        return Collections.unmodifiableList(stations);
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static boolean findByName(String name) {
        return stations.stream()
                .anyMatch(station -> station.getName().equals(name));
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst();
    }

    public static void delete(Station station) {
        stations.remove(station);
    }
}
