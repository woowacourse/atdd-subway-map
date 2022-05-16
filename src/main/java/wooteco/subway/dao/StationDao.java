package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.exception.notfound.NotFoundStationException;

@Repository
public class StationDao {

    private static final RowMapper<StationEntity> ROW_MAPPER = (resultSet, rowNum) -> new StationEntity(
            resultSet.getLong("id"),
            resultSet.getString("name"));

    private final SimpleJdbcInsert insertActor;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StationDao(final DataSource dataSource) {
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long save(final StationEntity station) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(station);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    public StationEntity findById(final Long id) {
        try {
            final String sql = "SELECT * FROM STATION WHERE id = :id";
            return namedParameterJdbcTemplate.queryForObject(sql, Map.of("id", id), ROW_MAPPER);
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundStationException();
        }
    }

    public List<StationEntity> findAll() {
        final String sql = "SELECT * FROM STATION";
        return namedParameterJdbcTemplate.query(sql, ROW_MAPPER);
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = :id";
        namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }

    public boolean existsById(final Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM STATION WHERE id = :id)";
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, Map.of("id", id), Boolean.class));
    }
}
