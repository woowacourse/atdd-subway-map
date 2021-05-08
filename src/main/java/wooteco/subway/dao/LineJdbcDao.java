package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineJdbcDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(final Line line) {
        String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(generatedId, line);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT l.id, l.name, l.color FROM LINE l";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String color = rs.getString("color");
            return new Line(id, name, color);
        });
    }

    @Override
    public Optional<Line> findByName(String name) {
        String sql = "SELECT l.id, l.name, l.color FROM LINE l WHERE l.name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long id = rs.getLong("id");
                final String color = rs.getString("color");
                return Optional.of(new Line(id, name, color));
            }, name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Line> findById(Long id) {
        String sql = "SELECT l.name, l.color FROM LINE l WHERE l.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final String name = rs.getString("name");
                final String color = rs.getString("color");
                return Optional.of(new Line(id, name, color));
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(final Long id, final Line line) {
        String sql = "UPDATE LINE l SET l.name = ?, l.color = ? WHERE l.id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM LINE l WHERE l.id = ?";
        jdbcTemplate.update(sql, id);
    }
}
