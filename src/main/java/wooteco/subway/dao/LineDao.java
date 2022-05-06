package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into line (name, color) values (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        long savedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Line(savedId, line.getName(), line.getColor());
    }

    public Optional<Line> findById(Long id) {
        String sql = "select * from line where id = ?";
        try {
            Line line = jdbcTemplate.queryForObject(sql, rowMapper(), id);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Line> findByName(String name) {
        String sql = "select * from line where name = ?";
        try {
            Line line = jdbcTemplate.queryForObject(sql, rowMapper(), name);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public void update(Long targetId, Line newLine) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), targetId);
    }

    public void delete(Line line) {
        String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, line.getId());
    }

    private RowMapper<Line> rowMapper() {
        return (rs, rowNum) ->
                new Line(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("color"));
    }
}
