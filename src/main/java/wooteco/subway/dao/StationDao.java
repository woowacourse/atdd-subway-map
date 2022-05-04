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

    private static final RowMapper<Station> STATION_ROW_MAPPER = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "INSERT INTO station (name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public Station findByName(String name) {
        String sql = "SELECT * FROM station WHERE name=?";
        return jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, name);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}
