package wooteco.subway.dao;

import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private static final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        new Station(resultSet.getLong("id"),
            resultSet.getString("name"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";

        List<Station> stations = jdbcTemplate.query(sql, new EmptySqlParameterSource(),
            stationRowMapper);
        return Collections.unmodifiableList(stations);
    }

    public Station save(Station station) {
        final String sql = "INSERT INTO station(name) VALUES (:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(station);

        jdbcTemplate.update(sql, paramSource, keyHolder);
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        jdbcTemplate.update(sql, paramSource);
    }

    public boolean existById(Long id) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM station WHERE id = :id)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }

    public boolean existByName(String name) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM station WHERE name = :name)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("name", name);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }

    public Station findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", id);

        return jdbcTemplate.queryForObject(sql, parameterSource, stationRowMapper);
    }
}
