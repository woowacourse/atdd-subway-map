package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.StationEntity;

@Repository
public class StationDao {

    private static final RowMapper<StationEntity> ROW_MAPPER = (resultSet, rowNum) ->
            new StationEntity(resultSet.getLong("id"),
                    resultSet.getString("name"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StationEntity> findAll() {
        final String sql = "SELECT * FROM station";

        return jdbcTemplate.query(sql, new EmptySqlParameterSource(), ROW_MAPPER);
    }

    public List<StationEntity> findAllByIds(List<Long> ids) {
        final String sql = "SELECT * FROM station WHERE id in (:ids)";
        SqlParameterSource params = new MapSqlParameterSource("ids", ids);

        return jdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public Optional<StationEntity> findById(Long id) {
        final String sql = "SELECT * FROM station WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER)
                .stream()
                .findFirst();
    }

    public Optional<StationEntity> findByName(String name) {
        final String sql = "SELECT * FROM station WHERE name = :name";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("name", name);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER)
                .stream()
                .findFirst();
    }

    public StationEntity save(StationEntity stationEntity) {
        final String sql = "INSERT INTO station(name) VALUES (:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(stationEntity);

        jdbcTemplate.update(sql, paramSource, keyHolder);
        Number generatedId = keyHolder.getKey();
        return new StationEntity(generatedId.longValue(), stationEntity.getName());
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        jdbcTemplate.update(sql, paramSource);
    }
}
