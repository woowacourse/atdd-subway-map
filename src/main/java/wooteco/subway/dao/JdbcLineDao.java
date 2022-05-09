package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_ID = "id";
    private static final String TABLE_NAME = "line";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert lineInserter;

    public JdbcLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(COLUMN_ID);
    }

    @Override
    public Line save(Line line) {
        Map<String, String> params = Map.of(COLUMN_NAME, line.getName(), COLUMN_COLOR, line.getColor());
        long insertedId = lineInserter.executeAndReturnKey(params).longValue();
        return setId(line, insertedId);
    }

    private Line setId(Line line, long id) {
        Field field = ReflectionUtils.findField(Line.class, COLUMN_ID);
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, line, id);
        return line;
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<Line> getRowMapper() {
        return (resultSet, rowNumber) -> {
            String name = resultSet.getString(COLUMN_NAME);
            String color = resultSet.getString(COLUMN_COLOR);
            long id = resultSet.getLong(COLUMN_ID);
            return setId(new Line(name, color), id);
        };
    }

    @Override
    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), id);
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
        return jdbcTemplate.update(sql, id);
    }
}
