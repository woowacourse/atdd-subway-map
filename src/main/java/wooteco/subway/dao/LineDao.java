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
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> rowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("line").usingGeneratedKeyColumns("id");

        String name = line.getName();
        String color = line.getColor();
        validateDuplicateName(name);
        validateDuplicateColor(color);
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Line(id, name, color);
    }

    private void validateDuplicateName(String name) {
        List<Line> lines = findAll();
        boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameName(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("이름이 중복된 노선은 만들 수 없습니다.");
        }
    }

    private void validateDuplicateColor(String color) {
        List<Line> lines = findAll();
        boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameColor(color));
        if (isDuplicate) {
            throw new IllegalArgumentException("색깔이 중복된 노선은 만들 수 없습니다.");
        }
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void update(Long id, String name, String color) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
