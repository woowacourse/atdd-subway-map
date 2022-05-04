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
import wooteco.subway.exception.InternalServerException;
import wooteco.subway.exception.NotFoundException;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> rowMapper = (resultSet, rowNumber) -> {
        Line line = new Line(
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return setId(line, resultSet.getLong("id"));
    };

    public JdbcLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Line setId(final Line line, final long id) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    @Override
    public Line save(final Line line) {
        try {
            final String sql = "INSERT INTO line SET name = ?, color = ?";

            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                final PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
                prepareStatement.setString(1, line.getName());
                prepareStatement.setString(2, line.getColor());
                return prepareStatement;
            }, keyHolder);
            final long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            return setId(line, id);
        } catch (final DuplicateKeyException e) {
            throw new IllegalArgumentException("중복된 이름의 노선은 저장할 수 없습니다.");
        }
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Line findById(final Long id) {
        try {
            final String sql = "SELECT * FROM line WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (final EmptyResultDataAccessException exception) {
            throw new NotFoundException("해당 ID에 맞는 노선을 찾지 못했습니다.");
        }
    }

    @Override
    public Line updateById(final Long id, final Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
        return setId(line, id);
    }

    @Override
    public Integer deleteById(final Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        final int affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            throw new InternalServerException("알 수 없는 이유로 노선을 삭제하지 못했습니다.");
        }
        return affectedRows;
    }
}
