package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static Station rowMapper(ResultSet rs, int row) throws SQLException {
        return new Station(rs.getLong("id"), rs.getString("name"));
    }

    public Station save(final Station station) {
        final String sql = "INSERT INTO STATION (name) VALUES (:name)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final long stationId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Station(stationId, station.getName());
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "SELECT id, name FROM STATION WHERE id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        final List<Station> stations = namedParameterJdbcTemplate.query(sql, params, StationDao::rowMapper);

        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public List<Station> findAll() {
        final String sql = "SELECT id, name FROM STATION";

        return namedParameterJdbcTemplate.query(sql, StationDao::rowMapper);
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = :id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public boolean existByName(final String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM STATION WHERE NAME = :name)";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class));
    }
}
