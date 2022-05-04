package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.Collections;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StationDao {

    private static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> {
        return new Station(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    };

    private static List<Station> stations = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Station station) {
        final String sql = "insert into STATION (name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        final String sql = "select id, name from STATION";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public static void delete(Long id) {
        boolean isRemoving = stations.removeIf(station -> station.getId().equals(id));
        if (!isRemoving) {
            throw new IllegalArgumentException("존재하지 않는 지하철역입니다.");
        }
    }

    public static void clear() {
        stations.clear();
    }
}
