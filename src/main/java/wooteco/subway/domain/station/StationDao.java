package wooteco.subway.domain.station;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.web.exception.SubwayHttpException;

@Repository
public class StationDao {

    private static final String STATION = "station";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String IDS = "ids";

    private static final RowMapper<Station> STATION_ROW_MAPPER = (rs, rowNum) ->
            new Station(
                    rs.getLong(ID),
                    rs.getString(NAME)
            );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public StationDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(STATION)
                .usingGeneratedKeyColumns(ID);
    }

    public Long save(Station station) {
        final MapSqlParameterSource params = getParamSource();
        params.addValue(NAME, station.getName());

        try {
            return simpleJdbcInsert.executeAndReturnKey(params).longValue();
        } catch (DuplicateKeyException e) {
            throw new SubwayHttpException("중복된 역 이름입니다");
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT id, name FROM station";
        return namedParameterJdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public Station findById(Long id) {
        final String sql = "SELECT id, name FROM station WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(ID, id);

        // todo 단일조회 예외처리 이슈
        return namedParameterJdbcTemplate.queryForObject(sql, params, STATION_ROW_MAPPER);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM station WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(ID, id);

        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<Station> findStationsByIds(Collection<Long> ids) {
        final String sql = "SELECT id, name FROM station WHERE id IN (:ids)";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(IDS, ids);

        return namedParameterJdbcTemplate.query(sql, params, STATION_ROW_MAPPER);
    }

    private MapSqlParameterSource getParamSource() {
        return new MapSqlParameterSource();
    }
}
