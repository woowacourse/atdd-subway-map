package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(final Line line) {
        final String sql = "insert into Line(name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new Line(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                line.getName(),
                line.getColor()
        );
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from Line";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public Optional<Line> findById(final Long id) {
        final String sql = "select id, name, color from Line where id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), id));
    }

    public Optional<Line> findByName(final String name) {
        final String sql = "select id, name, color from Line where name = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper(), name));
    }

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
        );
    }

    public void update(final Long id, final Line line) {
        final String sql = "update Line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void deleteById(final Long id) {
        final String sql = "delete from Line where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
