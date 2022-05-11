package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    
    private static final String NON_EXISTENT_ID_EXCEPTION = "존재하지 않는 id입니다.";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            Line.createWithoutSection(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("color"));

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", line.getName());
        parameters.put("color", line.getColor());

        final Number number = simpleJdbcInsert.executeAndReturnKey(parameters);
        return Line.createWithoutSection(number.longValue(), line.getName(), line.getColor());
    }

    public boolean existsByName(String name) {
        final String sql = "SELECT COUNT(*) FROM line WHERE name = ?";
        final Integer numOfLine = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return !numOfLine.equals(0);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        final Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        if (line == null) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }
        return line;
    }

    public void updateLineById(Long id, String name, String color) {
        final String sql = "UPDATE line SET name=?, color=? WHERE id=?";
        validateResult(jdbcTemplate.update(sql, name, color, id));
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        validateResult(jdbcTemplate.update(sql, id));
    }

    private void validateResult(int result) {
        if (result == 0) {
            throw new IllegalArgumentException(NON_EXISTENT_ID_EXCEPTION);
        }
    }
}
