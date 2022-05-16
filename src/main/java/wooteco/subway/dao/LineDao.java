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
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.exception.notfound.NotFoundLineException;

@Repository
public class LineDao {

    private static final RowMapper<LineEntity> ROW_MAPPER = (resultSet, rowNum) -> new LineEntity(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"));

    private final SimpleJdbcInsert insertActor;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(final DataSource dataSource) {
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long save(final LineEntity line) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    public List<LineEntity> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public LineEntity find(final Long id) {
        try {
            final String sql = "SELECT * FROM LINE WHERE id = :id";
            return jdbcTemplate.queryForObject(sql, Map.of("id", id), ROW_MAPPER);
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundLineException();
        }
    }

    public void update(final LineEntity line) {
        final String sql = "UPDATE LINE SET name = :name, color = :color WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", line.getId(), "name", line.getName(), "color", line.getColor()));
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    public boolean existsById(final Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id = :id)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Map.of("id", id), Boolean.class));
    }
}
