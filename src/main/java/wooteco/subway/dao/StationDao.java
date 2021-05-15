package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StationDao implements StationRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (rs, rn) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Station(id, name);
    };

    @Override
    public Station save(Station station) {
        String query = "INSERT INTO STATION (name) VALUES (:name)";

        Map<String, Object> param = new HashMap<>();
        param.put("name", station.getName());

        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(param);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(query, mapSqlParameterSource, keyHolder);

        return this.findById(keyHolder.getKey().longValue());
    }

    @Override
    public Station findById(long id) {
        String query = "SELECT * FROM STATION WHERE id = :id";
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);

        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(param);

        return this.jdbcTemplate.queryForObject(query, mapSqlParameterSource, stationRowMapper);
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM STATION";
        return this.jdbcTemplate.query(query, stationRowMapper);
    }

    @Override
    public Optional<Station> findByName(String name) {
        String query = "SELECT * FROM STATION WHERE name = :name";

        Map<String, Object> param = new HashMap<>();
        param.put("name", name);

        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(param);

        return this.jdbcTemplate.query(query, mapSqlParameterSource, (rs) -> {
            if (rs.next()) {
                long id = rs.getLong("id");
                String stationName = rs.getString("name");
                return Optional.of(new Station(id, stationName));
            }
            return Optional.empty();
        });
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM STATION WHERE id = :id";

        Map<String, Object> param = new HashMap<>();
        param.put("id", id);

        SqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(param);

        jdbcTemplate.update(query, mapSqlParameterSource);
    }
}
