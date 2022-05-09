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

@Repository
public class StationDao implements CommonStationDao {

    private static final int NO_ROW_AFFECTED = 0;
    private static final String STATION_DUPLICATED = "이미 존재하는 지하철역입니다. ";
    private static final String STATION_NOT_FOUND = "요청한 지하철 역이 존재하지 않습니다. ";

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
        try {
            final Long id = simpleInsert.executeAndReturnKey(params).longValue();
            return new Station(id, station.getName());
        } catch (DuplicateKeyException e) {
            throw new IllegalStateException(STATION_DUPLICATED + station);
        }
    }

    @Override
    public List<Station> findAll() {
        final String sql = "select id, name from STATION";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Station(resultSet.getLong("id"), resultSet.getString("name"));
        });
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "delete from STATION where id = :id";
        final int theNumberOfAffectedRow = namedParameterJdbcTemplate.update(sql, Map.of("id", id));
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(STATION_NOT_FOUND + "id=" + id);
        }
    }
}
