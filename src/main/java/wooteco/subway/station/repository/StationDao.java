package wooteco.subway.station.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Repository
public class StationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = namedParameterJdbcTemplate;
    }

    public Station save(Station station) {
        final String sql = "INSERT INTO STATION (name) VALUES (:name)";

        SqlParameterSource parameters = new MapSqlParameterSource("name", station.getName());

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, parameters, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);

        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM STATION";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");

            return new Station(id, name);
        });
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM STATION WHERE id = :id";

        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameters);
    }

    public Station findById(final Long id) {
        final String sql = "SELECT * FROM STATION WHERE id = :id";

        SqlParameterSource parameters = new MapSqlParameterSource("id", id);

        return jdbcTemplate.queryForObject(sql, parameters, (rs, rn) -> {
            String name = rs.getString("name");
            return new Station(id, name);
        });
    }

    public List<Station> findByIds(final List<Long> ids) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);

        final String sql = "SELECT * FROM STATION WHERE id IN (:ids)";

        return jdbcTemplate.query(sql, parameters,
                (rs, rn) -> new Station(rs.getLong("id"), rs.getString("name")));
    }
}
