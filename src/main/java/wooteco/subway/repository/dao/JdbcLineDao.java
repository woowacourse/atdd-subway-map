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
import wooteco.subway.repository.entity.LineEntity;

@Repository
public class JdbcLineDao implements LineDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<LineEntity> rowMapper = (resultSet, rowNum) -> new LineEntity(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    public JdbcLineDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LineEntity save(final LineEntity lineEntity) {
        final String sql = "INSERT INTO LINE(name, color) VALUES(:name, :color)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final SqlParameterSource source = new BeanPropertySqlParameterSource(lineEntity);
        jdbcTemplate.update(sql, source, keyHolder);
        if (keyHolder.getKey() == null) {
            throw new RuntimeException("[ERROR] Line 테이블에 레코드 추가 후에 key가 반환되지 않았습니다.");
        }
        final long id = keyHolder.getKey().longValue();
        return new LineEntity(id, lineEntity.getName(), lineEntity.getColor());
    }

    @Override
    public List<LineEntity> findAll() {
        final String sql = "SELECT id, name, color FROM LINE";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<LineEntity> findByName(final String name) {
        final String sql = "SELECT id, name, color FROM LINE WHERE name = :name";
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        try {
            final LineEntity lineEntity = jdbcTemplate.queryForObject(sql, source, rowMapper);
            return Optional.of(lineEntity);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<LineEntity> findById(final Long id) {
        final String sql = "SELECT id, name, color FROM LINE WHERE id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        try {
            final LineEntity lineEntity = jdbcTemplate.queryForObject(sql, source, rowMapper);
            return Optional.of(lineEntity);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        final SqlParameterSource source = new MapSqlParameterSource(params);
        jdbcTemplate.update(sql, source);
    }

    @Override
    public void update(final LineEntity newLineEntity) {
        final String sql = "UPDATE LINE SET "
                + "name = :name, "
                + "color = :color "
                + "WHERE id = :id";
        final SqlParameterSource source = new BeanPropertySqlParameterSource(newLineEntity);
        jdbcTemplate.update(sql, source);
    }

    @Override
    public Boolean existByNameExcludeId(final Long id, final String name) {
        final String sql = "SELECT COUNT(id) AS same_name_count FROM LINE WHERE name = :name AND id != :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("id", id);
        final SqlParameterSource source = new MapSqlParameterSource(params);

        final Integer count = jdbcTemplate.queryForObject(sql, source,
                (resultSet, rowNumber) -> resultSet.getInt("same_name_count"));
        return count != 0;
    }
}
