package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

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
    public Line findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id=?";
        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    Line line = new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color")
                    );
                    return line;
                }, id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}
