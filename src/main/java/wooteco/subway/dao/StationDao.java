package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;
    private final JdbcTemplate jdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource dataSource,
                      JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());
        final Long id = simpleInsert.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        final String sql = "select id, name from STATION";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Station(resultSet.getLong("id"), resultSet.getString("name"));
        });
    }

    public Station findById(Long id) {
        final String sql = "select id, name from STATION where id = :id";
        SqlParameterSource parameter = new MapSqlParameterSource(Map.of("id", id));
        return namedParameterJdbcTemplate.queryForObject(sql, parameter, (resultSet, rowNum) -> {
            return new Station(resultSet.getLong("id"), resultSet.getString("name"));
        });
    }

    public void deleteById(Long id) {
        final String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        final String sql = "delete from STATION";
        jdbcTemplate.update(sql);
    }
}
