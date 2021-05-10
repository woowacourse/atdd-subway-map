package wooteco.subway.line.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class LineH2Dao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Line line = new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color")
                    );
                    return line;
                });
    }

    @Override
    public void update(Long id, String name, String color) {
        String sql = "UPDATE LINE SET name=?, color=? WHERE id=?";
        jdbcTemplate.update(sql, name, color, id);
    }

    @Override
    public Optional<Line> findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {
                        return new Line(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("color")
                        );
                    },
                    id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Line> findByName(String name) {
        String sql = "SELECT * FROM LINE WHERE name=?";
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Line line = new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
                );
                return line;
            }, name)
            .stream()
            .findAny();
    }

    @Override
    public Optional<Line> findByColor(String color) {
        String sql = "SELECT * FROM LINE WHERE color=?";
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Line line = new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
                );
                return line;
            }, color)
            .stream()
            .findAny();
    }
}
