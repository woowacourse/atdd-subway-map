package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.StationEntity;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInserter;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcStationDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public StationEntity save(StationEntity entity) {
        final Map<String, String> params = Map.of("name", entity.getName());

        long savedId = simpleInserter.executeAndReturnKey(params).longValue();
        return new StationEntity(savedId, entity.getName());
    }

    @Override
    public List<StationEntity> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<StationEntity> getRowMapper() {
        return (resultSet, rowNumber) -> {
            String name = resultSet.getString("name");
            long id = resultSet.getLong("id");
            return new StationEntity(id, name);
        };
    }

    @Override
    public Integer deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<StationEntity> findByIds(List<Long> ids) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String query = "SELECT * FROM station where id in (:ids)";
        return namedParameterJdbcTemplate.query(query, parameters, getRowMapper());
    }

    @Override
    public Optional<StationEntity> findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, getRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
