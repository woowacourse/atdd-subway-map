package wooteco.subway.line;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(final Line line) {
        final String sql = "INSERT INTO line (name, color) VALUES (?, ?)";
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final PreparedStatementCreator preparedStatementCreator = con -> {
            final PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        final long id = keyHolder.getKey().longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        final List<Line> lines = jdbcTemplate.query(sql, lineRowMapper, id);
        if (lines.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(lines.get(0));
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        final List<Line> lines = jdbcTemplate.query(sql, lineRowMapper, name);
        if (lines.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(lines.get(0));
    }

    public void update(final Line updatedLine) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, updatedLine.getName(), updatedLine.getColor(), updatedLine.getId());
    }
}
