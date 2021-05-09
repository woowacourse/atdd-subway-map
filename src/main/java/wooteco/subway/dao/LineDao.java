package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LineDao {

    private static final RowMapper<Line> LINE_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("color"),
            resultSet.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(Line line) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>(2);
        params.put("color", line.getColor());
        params.put("name", line.getName());
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return jdbcTemplate.query(query, LINE_MAPPER);
    }

    public Line findById(Long id) {
        String query = "SELECT id, color, name FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(query, LINE_MAPPER, id);
    }

    public String findNameById(Long id) {
        String query = "SELECT name FROM line WHERE id=?";
        return jdbcTemplate.queryForObject(query, String.class, id);
    }

    public String findColorById(Long id) {
        String query = "SELECT color FROM line WHERE id=?";
        return jdbcTemplate.queryForObject(query, String.class, id);
    }

    public void update(Long id, Line line) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        jdbcTemplate.update(query, line.getColor(), line.getName(), id);
    }

    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public boolean isExistById(Long id) {
        String query = "SELECT COUNT(name) FROM line WHERE id =?";
        final int counts = jdbcTemplate.queryForObject(query, Integer.class, id);
        return counts > 0;
    }

    public boolean isExistByName(String name) {
        String query = "SELECT COUNT(name) FROM line WHERE name =?";
        final int counts = jdbcTemplate.queryForObject(query, Integer.class, name);
        return counts > 0;
    }

    public boolean isExistByColor(String color) {
        String query = "SELECT COUNT(name) FROM line WHERE color =?";
        final int counts = jdbcTemplate.queryForObject(query, Integer.class, color);
        return counts > 0;
    }
}
