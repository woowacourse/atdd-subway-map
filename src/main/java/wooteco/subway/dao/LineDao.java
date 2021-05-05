package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.line.Line;
import org.springframework.jdbc.core.RowMapper;

@Repository
public class LineDao {
    private static final RowMapper<Line> ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        return new Line(id, name, color);
    };
    private final JdbcTemplate jdbcTemplate;
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        String saveQuery = "INSERT INTO LINE (name, color) VALUES (? ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(saveQuery, new String[]{"id"});
            prepareStatement.setString(1, line.getName());
            prepareStatement.setString(2, line.getColor());
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, ROW_MAPPER);
    }

    public Line findById(long id) {
        String query = "SELECT * FROM WHERE id = ?";
        return jdbcTemplate.queryForObject(query, ROW_MAPPER, id);
    }

    public void updateLine(Line line, String name, String color) {
        Field nameField = ReflectionUtils.findField(Line.class, "name");
        nameField.setAccessible(true);
        ReflectionUtils.setField(nameField, line, name);
        Field colorField = ReflectionUtils.findField(Line.class, "color");
        colorField.setAccessible(true);
        ReflectionUtils.setField(colorField, line, color);
    }

    public void deleteById(long id) {
        lines.removeIf(Line -> Line.getId() == id);
    }
}
