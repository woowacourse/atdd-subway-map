package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NoStationFoundException;
import wooteco.subway.exception.StationDuplicateException;

@Repository
public class StationDao {

    private static final int NO_ROW_AFFECTED = 0;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station save(final Station station) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());
        try {
            final Long id = simpleInsert.executeAndReturnKey(params).longValue();
            return new Station(id, station.getName());
        } catch (DuplicateKeyException e) {
            throw new StationDuplicateException(station.toString());
        }
    }

    public List<Station> findAll() {
        final String sql = "select id, name from STATION";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Station(resultSet.getLong("id"), resultSet.getString("name"));
        });
    }

    public void deleteById(final Long id) {
        final String sql = "delete from STATION where id = :id";
        final int theNumberOfAffectedRow = namedParameterJdbcTemplate.update(sql, Map.of("id", id));
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new NoStationFoundException("id=" + id);
        }
    }
}
