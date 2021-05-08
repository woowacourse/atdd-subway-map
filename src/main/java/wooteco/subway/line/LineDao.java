package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class LineDao {

    public static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) -> new Line(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("color"));
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
    }

    public void updateById(Long id, Line line) {
        String sql = "UPDATE line SET name = (?), color = (?) WHERE id = (?) ";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    public int countName(String name) {
        String sql = "SELECT count(*) FROM line WHERE `name` = (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }

    public int countColor(String color) {
        String sql = "SELECT count(*) FROM line WHERE color = (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, color);
    }

    public int countId(Long id) {
        String sql = "SELECT count(*) FROM line WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    public int countNameWithDifferentId(String name, Long id) {
        String sql = "SELECT count(*) FROM line WHERE `name` = (?) AND id <> (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, name, id);
    }

    public int countColorWithDifferentId(String color, Long id) {
        String sql = "SELECT count(*) FROM line WHERE color = (?) AND id <> (?)";
        return jdbcTemplate.queryForObject(sql, Integer.class, color, id);
    }
}
