package wooteco.subway.line.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

@Repository
public class LineDao implements LineRepository {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    private RowMapper<Line> rowMapper = (rs, rn) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color"),
                    new ArrayList<>()
            );

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(final Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        final Long id = keyHolder.getKeyAs(Long.class);
        return new Line(id, line.getName(), line.getColor(), new ArrayList<>());
    }

    @Override
    public Optional<Line> findByName(final String name) {
        final String sql = "SELECT * FROM LINE WHERE name = ?";
        final List<Line> result = jdbcTemplate.query(sql, rowMapper, name);
        return optionalOf(result);
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";
        final List<Line> result = jdbcTemplate.query(sql, rowMapper, id);
        return optionalOf(result);
    }

    private Optional<Line> optionalOf(final List<Line> result) {
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void update(final Line line) {
        final String sql = "UPDATE LINE SET NAME = ?, COLOR =? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
