package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.LineNotExistException;

import java.util.*;

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

    public Line insert(String color, String name) {
        final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>(2);
        params.put("color", color);
        params.put("name", name);
        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Line(id, color, name);
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM line";
        return jdbcTemplate.query(query, LINE_MAPPER);
    }

    public Line findById(Long id) {
        String query = "SELECT * FROM line WHERE id = ?";
        final Line line = DataAccessUtils.singleResult(jdbcTemplate.query(query, LINE_MAPPER, id));
        if (Objects.isNull(line)) {
            throw new LineNotExistException();
        }
        return line;
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
