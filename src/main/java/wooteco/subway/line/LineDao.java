package wooteco.subway.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final KeyHolder keyHolder = new GeneratedKeyHolder();

    public LineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final String name, final String color) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void update(final Long id, final String name, final String color) {
        final String sql = "UPDATE LINE SET NAME = ?, COLOR =? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public Line findById(final Long id) {
        try {
            final String sql = "SELECT * FROM LINE WHERE id = ?";
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new Line(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("color")
                    ),
                    id);
        } catch (Exception e) {
            throw new LineException("존재하지 않는 노선입니다.");
        }
    }

    public boolean isDuplicatedName(final String name) {
        final String sql = "SELECT EXISTS(SELECT from LINE WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(
                sql,
                (rs, rn) -> new Line(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("color")
                )
        );
    }
}
