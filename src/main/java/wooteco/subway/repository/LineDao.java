package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.notFoundException.LineNotFoundException;

@Repository
public class LineDao {

    private static final RowMapper<Line> ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        return new Line(id, name, color);
    };
    private static final int AFFECTED_NONE = 0;

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        String saveQuery = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection
                .prepareStatement(saveQuery, new String[]{"id"});
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

    public Optional<Line> findById(Long id) {
        String query = "SELECT id, name, color FROM LINE WHERE id = ?";
        return jdbcTemplate.query(query, ROW_MAPPER, id)
            .stream()
            .findAny();
    }

    public void updateLine(Line line) {
        String query = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        int rowCounts = jdbcTemplate.update(query, line.getName(), line.getColor(), line.getId());
        if (rowCounts == 0) {
            throw new LineNotFoundException();
        }
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM LINE WHERE ID = ?";
        int rowCounts = jdbcTemplate.update(query, id);
        if (rowCounts == AFFECTED_NONE) {
            throw new LineNotFoundException();
        }
    }
}
