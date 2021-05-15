package wooteco.subway.line.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rn) -> {
                final String name = rs.getString("name");
                final String color = rs.getString("color");

                return Optional.of(new Line(id, name, color));
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "SELECT * FROM LINE WHERE name = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rn) -> {
                final Long id = rs.getLong("id");
                final String color = rs.getString("color");

                return Optional.of(new Line(id, name, color));
            }, name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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
