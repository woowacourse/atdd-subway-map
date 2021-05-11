package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("color"),
            resultSet.getString("name")
    );

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line insert(Line line) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        String color = line.getColor();
        String name = line.getName();

        Map<String, Object> params = new HashMap<>(2);
        params.put("color", color);
        params.put("name", name);
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Line(id, color, name);
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return jdbcTemplate.query(query, LINE_ROW_MAPPER);
    }

    public Optional<Line> findById(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.query(query, LINE_ROW_MAPPER, id)
                .stream()
                .findAny();
    }

    public Optional<Line> findByName(String name) {
        String query = "SELECT * FROM line WHERE name = ?";
        return jdbcTemplate.query(query, LINE_ROW_MAPPER, name)
                .stream()
                .findAny();
    }

    public Optional<Line> findByColor(String color) {
        String query = "SELECT * FROM line WHERE color = ?";
        return jdbcTemplate.query(query, LINE_ROW_MAPPER, color)
                .stream()
                .findAny();
    }

    public void update(Long id, String color, String name) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        jdbcTemplate.update(query, color, name, id);
    }

    public void delete(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
