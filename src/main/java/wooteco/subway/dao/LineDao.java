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

    private static final RowMapper<Line> LINE_ROW_MAPPER = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, line.getName());
            statement.setString(2, line.getColor());
            return statement;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM line WHERE id=?";
        return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public void update(Long id, String name, String color) {
        String sql = "UPDATE line SET name=?, color=? WHERE id=?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}
