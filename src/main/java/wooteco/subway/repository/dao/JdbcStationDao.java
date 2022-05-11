package wooteco.subway.repository.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.StationEntity;

@Repository
public class JdbcStationDao implements StationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<StationEntity> rowMapper = (resultSet, rowNum) -> new StationEntity(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public JdbcStationDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public StationEntity save(final StationEntity stationEntity) {
        final String sql = "INSERT INTO STATION(name) VALUES(:name)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource source = new BeanPropertySqlParameterSource(stationEntity);
        jdbcTemplate.update(sql, source, keyHolder);
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("[ERROR] Station 테이블에 레코드 추가 후에 key가 반환되지 않았습니다.");
        }
        final long id = keyHolder.getKey().longValue();
        return new StationEntity(id, stationEntity.getName());
    }

    @Override
    public List<StationEntity> findAll() {
        final String sql = "SELECT id, name FROM STATION";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<StationEntity> findByName(final String name) {
        final String sql = "SELECT id, name FROM STATION WHERE name = :name";
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        try {
            final StationEntity stationEntity = jdbcTemplate.queryForObject(sql, source, rowMapper);
            return Optional.ofNullable(stationEntity);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<StationEntity> findById(final Long id) {
        final String sql = "SELECT id, name FROM STATION WHERE id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        try {
            final StationEntity stationEntity = jdbcTemplate.queryForObject(sql, source, rowMapper);
            return Optional.ofNullable(stationEntity);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM STATION WHERE id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        jdbcTemplate.update(sql, source);
    }
}
