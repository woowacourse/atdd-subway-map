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
            rs.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        final String sql = "INSERT INTO station (name) VALUES (?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean hasStation(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM station WHERE name = ?);";
        return jdbcTemplate.queryForObject(sql, Boolean.class ,name);
    }

    public Station findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = ?;";
        return jdbcTemplate.queryForObject(sql, STATION_ROW_MAPPER, id);
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public void delete(Long id) {
        final String sql = "delete from station where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
