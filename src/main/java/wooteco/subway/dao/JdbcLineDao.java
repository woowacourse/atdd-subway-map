package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private static final String TABLE_NAME = "line";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInserter;

    public JdbcLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Line save(Line line) {
        Map<String, String> params = Map.of("name", line.getName(), "color", line.getColor());
        long insertedId = simpleInserter.executeAndReturnKey(params).longValue();
        return setId(line, insertedId);
    }

    private Line setId(Line line, long id) {
        Field field = ReflectionUtils.findField(Line.class, "id");
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
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            long id = resultSet.getLong("id");
            return setId(new Line(name, color), id);
        };
    }

    @Override
    public Optional<Line> findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, getRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Line> update(Line line) {
        String sql = "UPDATE line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        return findById(line.getId());
    }

    @Override
    public Integer deleteById(Long id) {
        String sql = "DELETE FROM line where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
