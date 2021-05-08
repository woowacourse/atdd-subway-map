package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StationDao {

    private static final RowMapper<Station> STATION_MAPPER = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Long insert(Station station) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("name", station.getName());
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return jdbcTemplate.query(query, STATION_MAPPER);
    }

    public String findNameById(Long id) {
        String query = "SELECT name FROM station WHERE id=?";
        return jdbcTemplate.queryForObject(query, String.class, id);
    }

    public Optional<Station> findById(Long id) {
        String query = "SELECT * FROM station WHERE id=?";
        return jdbcTemplate.query(query, STATION_MAPPER, id)
                .stream()
                .findAny();
    }

    public Optional<Station> findByName(String name) {
        String query = "SELECT * FROM station WHERE name=?";
        return jdbcTemplate.query(query, STATION_MAPPER, name)
                .stream()
                .findAny();
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public int countsByName(String name) {
        String query = "SELECT COUNT(name) FROM station WHERE name =?";
        return jdbcTemplate.queryForObject(query, Integer.class, name);
    }
}
