package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Station> STATION_ROW_MAPPER = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public Long save(Station station) {
        String sql = "INSERT INTO station (name) values (?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Station findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, id);
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";

        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }
}
