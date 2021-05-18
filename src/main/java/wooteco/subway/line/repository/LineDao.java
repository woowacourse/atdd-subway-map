package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Lines;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("color"),
            resultSet.getString("name")
    );

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(final Line line) {
        String query = "INSERT INTO line(color, name) VALUES(?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, line.getColor());
            ps.setString(2, line.getName());
            return ps;
        }, keyHolder);

        return new Line(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                line.getColor(),
                line.getName()
        );
    }

    public Lines findAll() {
        String query = "SELECT id, color, name FROM line ORDER BY id";
        List<Line> lines = jdbcTemplate.query(query, lineRowMapper);
        return new Lines(lines);
    }

    public Optional<Line> findById(final Long id) {
        String query = "SELECT id, color, name FROM line WHERE id = ?";
        return jdbcTemplate.query(query, lineRowMapper, id)
                .stream()
                .findFirst();
    }

    public void update(final Line line) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        jdbcTemplate.update(query, line.getColor(), line.getName(), line.getId());
    }

    public void deleteById(final Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
