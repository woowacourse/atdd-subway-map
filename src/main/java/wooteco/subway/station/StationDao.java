package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicationException;

@Repository
public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        validateDuplicatedName(station);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO station (name) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                .prepareStatement(sql, new String[]{"id", "name"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        return new Station((Long) keys.get("id"), (String) keys.get("name"));
    }

    private static void validateDuplicatedName(Station station) {
        if (isDuplicate(station)) {
            throw new DuplicationException("이미 존재하는 역 이름입니다.");
        }
    }

    private static boolean isDuplicate(Station newStation) {
        return stations.stream()
            .anyMatch(station -> station.isSameName(newStation));
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteAll() {
        stations.clear();
    }
}
