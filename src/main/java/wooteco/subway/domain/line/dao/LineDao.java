package wooteco.subway.domain.line.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Line> lineRowMapper = (rs, rowNum) -> new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> save(Line line) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO line (name, color) VALUES (?, ?)";

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, line.getName());
                ps.setString(2, line.getColor());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            return Optional.empty();
        }

        return Optional.of(keyHolder.getKey().longValue());
    }

    public List<Line> findAll() {
        final String sql = "SELECT id, name, color FROM line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(Long id) {
        final String sql = "SELECT id, name, color FROM line WHERE id = ?";
        try {
            Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);
            return Optional.of(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(Long id, Line line) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
