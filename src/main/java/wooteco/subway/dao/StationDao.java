package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.Station;

@Component
public class StationDao implements CommonStationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Station save(final Station station) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());
        final Long id = simpleInsert.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());

    }

    @Override
    public List<Station> findAll() {
        final String sql = "select id, name from STATION";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Station(resultSet.getLong("id"), resultSet.getString("name"));
        });
    }

    @Override
    public Station findById(final Long id) {
        final String sql = "select id, name from STATION WHERE id=:id";
        final SqlParameterSource parameter = new MapSqlParameterSource(Map.of("id", id));
        return namedParameterJdbcTemplate.queryForObject(sql, parameter, (resultSet, rowNum) -> {
            return new Station(resultSet.getLong("id"), resultSet.getString("name"));
        });
    }

    @Override
    public int deleteById(final Long id) {
        final String sql = "delete from STATION where id = :id";
        return namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }
}
