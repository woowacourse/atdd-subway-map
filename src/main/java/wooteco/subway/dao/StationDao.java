package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }

    public Station save(Station station) {
        Map<String, String> params = Map.of("name", station.getName());
        long savedId = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Station(savedId, station.getName());
    }

    public Optional<Station> findById(Long id) {
        String sql = "select id, name from station where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        try {
            Station station = jdbcTemplate.queryForObject(sql, namedParameters, rowMapper());
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Station> findByName(String name) {
        String sql = "select id, name from station where name = :name";
        SqlParameterSource namedParameters = new MapSqlParameterSource("name", name);
        try {
            Station station = jdbcTemplate.queryForObject(sql, namedParameters, rowMapper());
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Station> findAll() {
        String sql = "select id, name from station";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public void delete(Station station) {
        String sql = "delete from station where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", station.getId());
        jdbcTemplate.update(sql, namedParameters);
    }

    private RowMapper<Station> rowMapper() {
        return (rs, rowNum) ->
                new Station(rs.getLong("id"),
                        rs.getString("name"));
    }
}
