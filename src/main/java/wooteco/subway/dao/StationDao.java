package wooteco.subway.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedNameException;

@Repository
public class StationDao {

    private static final String STATION = "station";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String IDS = "ids";
    private static final String STATION_RESOURCE_NAME = "역";

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
            throw new DuplicatedNameException(STATION_RESOURCE_NAME);
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT id, name FROM station";
        return namedParameterJdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public Optional<Station> findById(Long id) {
        final String sql = "SELECT id, name FROM station WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(ID, id);

        try {
            Station station = namedParameterJdbcTemplate
                    .queryForObject(sql, params, STATION_ROW_MAPPER);
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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
