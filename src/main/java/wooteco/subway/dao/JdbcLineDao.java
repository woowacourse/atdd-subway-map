package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.InternalServerException;
import wooteco.subway.exception.NotFoundException;

@Repository
public class JdbcLineDao implements LineDao {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_ID = "id";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> rowMapper = (resultSet, rowNumber) -> {
        Line line = new Line(
                resultSet.getString(COLUMN_NAME),
                resultSet.getString(COLUMN_COLOR)
        );
        return setId(line, resultSet.getLong(COLUMN_ID));
    };

    public JdbcLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Line setId(Line line, long id) {
        Field field = ReflectionUtils.findField(Line.class, COLUMN_ID);
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    @Override
    public Line save(Line line) {
        try {
            final String sql = "INSERT INTO line SET name = ? , color = ?";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{COLUMN_ID});
                prepareStatement.setString(1, line.getName());
                prepareStatement.setString(2, line.getColor());
                return prepareStatement;
            }, keyHolder);
            long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            return setId(line, id);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Line findById(Long id) {
        try {
            final String sql = "SELECT * FROM line WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException(ExceptionMessage.NOT_FOUND_LINE_BY_ID.getContent());
        }
    }

    @Override
    public Line updateById(Long id, Line line) {
        String sql = "UPDATE line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
        return setId(line, id);
    }

    @Override
    public Integer deleteById(Long id) {
        String sql = "DELETE FROM line where id = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            throw new InternalServerException(ExceptionMessage.UNKNOWN_DELETE_LINE_FAIL.getContent());
        }
        return affectedRows;
    }
}
