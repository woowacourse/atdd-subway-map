package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, line.getName());
            pstmt.setString(2, line.getColor());
            return pstmt;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        return new Line(id, line.getName(), line.getColor(), line.getSections());
    }

    public List<Line> allLines() {
        final String sql = "SELECT * FROM LINE";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String color = rs.getString("color");
            return new Line(id, name, color, null);
        });
    }

    public Line findById(final Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rn) -> {
            final String name = rs.getString("name");
            final String color = rs.getString("color");

            return new Line(id, name, color, null);
        }, id);
    }

    public void update(final Line line) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";

        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";

        jdbcTemplate.update(sql, id);
    }
}
