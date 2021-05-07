package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class LineDao {

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
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("color"),
                        resultSet.getString("name")
                )
        );
    }

    public Optional<Line> findById(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("color"),
                        resultSet.getString("name")
                ), id)
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
