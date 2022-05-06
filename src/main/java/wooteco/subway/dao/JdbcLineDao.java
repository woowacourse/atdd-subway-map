package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> rowMapper = (resultSet, rowNumber) -> {
        Line line = new Line(
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return setId(line, resultSet.getLong("id"));
    };

    public JdbcLineDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    private Line setId(final Line line, final long id) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    @Override
    public Optional<Line> save(final Line line) {
        try {
            final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
            final long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
            return Optional.of(setId(line, id));
        } catch (final DuplicateKeyException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Line> findById(final Long id) {
        try {
            final String sql = "SELECT * FROM line WHERE id = ?";
            final Line line = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.of(line);
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Line updateById(final Long id, final Line line) {
        try {
            final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
            final int affectedRows = jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
            checkAffectedRows(affectedRows);
            return setId(line, id);
        } catch (final DuplicateKeyException e) {
            throw new IllegalArgumentException("중복된 이름의 노선이 존재합니다.");
        }
    }

    private void checkAffectedRows(final int affectedRows) {
        if (affectedRows == 0) {
            throw new IllegalArgumentException("id가 일치하는 노선이 존재하지 않습니다.");
        }
    }

    @Override
    public Integer deleteById(final Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        final int affectedRows = jdbcTemplate.update(sql, id);
        checkAffectedRows(affectedRows);
        return affectedRows;
    }
}
