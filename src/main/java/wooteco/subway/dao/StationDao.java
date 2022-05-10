package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Station> rowMapper = (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name"));

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "INSERT INTO station (name) VALUES (:name)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        long stationId = keyHolder.getKey().longValue();

        return new Station(stationId, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(), rowMapper);
    }

    public Optional<Station> findByName(String name) {
        String sql = "SELECT id, name FROM station WHERE name = :name";

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        List<Station> stations = jdbcTemplate.query(sql, params, rowMapper);

        return stations.stream().findFirst();
    }

    public Optional<Station> findById(Long id) {
        String sql = "SELECT id, name FROM station WHERE id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        List<Station> stations = jdbcTemplate.query(sql, new MapSqlParameterSource(params), rowMapper);

        return stations.stream().findFirst();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id=:id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        int affected = jdbcTemplate.update(sql, params);

        if (affected == 0) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }
}
