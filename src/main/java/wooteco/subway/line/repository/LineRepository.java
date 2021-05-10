package wooteco.subway.line.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class LineRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("color"),
            resultSet.getString("name")
    );

    public LineRepository(final JdbcTemplate jdbcTemplate) {
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

    public List<Line> getLines() {
        String query = "SELECT id, color, name FROM line ORDER BY id";
        return jdbcTemplate.query(query, lineRowMapper);
    }

    public Line getLine(final Long id) {
        String query = "SELECT id, color, name FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(query, lineRowMapper, id);
    }

    public void update(final Line line) {
        String query = "UPDATE line SET color = ?, name = ? WHERE id = ?";
        jdbcTemplate.update(query, line.getColor(), line.getName(), line.getId());
    }

    public void deleteById(final Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    public boolean isNameExist(final Line line) {
        String query = "SELECT EXISTS(SELECT * FROM Line WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, line.getName());
    }

    public boolean isIdNotExist(final Line line) {
        String query = "SELECT NOT EXISTS(SELECT * FROM Line WHERE id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, line.getId());
    }

    public boolean isIdNotExist(final Long id) {
        String query = "SELECT NOT EXISTS(SELECT * FROM Line WHERE id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, id);
    }
}
