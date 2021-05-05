package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {
    private static final RowMapper<Line> ROW_MAPPER = (resultSet, rowNumber) -> {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        return new Line(id, name, color);
    };

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        String query = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query, new String[]{"id"});
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
        String query = "SELECT * FROM LINE WHERE ID = ?";
        return jdbcTemplate.queryForObject(query, ROW_MAPPER, id);
    }

    public void update(long id, String name, String color) {
        String query = "UPDATE LINE SET name = ?, color = ? WHERE ID = ?";
        jdbcTemplate.update(query, name, color, id);
    }

    public void deleteById(long id) {
        String query = "DELETE FROM LINE WHERE ID = ?";
        jdbcTemplate.update(query, id);
    }
}
