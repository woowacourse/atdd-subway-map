package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    public Long save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES(?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, line.getName());
            statement.setString(2, line.getColor());
            return statement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public Long updateByLine(Line line) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";

        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        return line.getId();
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
