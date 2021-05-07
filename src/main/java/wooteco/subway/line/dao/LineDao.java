package wooteco.subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO line (name, color) VALUES (?, ?)";

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color")
        ));
    }

    public void update(Long id, Line line) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void clear() {
        String sql = "DELETE FROM line";
        jdbcTemplate.update(sql);
    }

    public int countLineByName(String name) {
        String sql = "SELECT count(*) FROM line WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, name);
    }
}
