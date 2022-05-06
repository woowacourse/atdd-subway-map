package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowMapper) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO LINE(name, color) VALUES(?, ?)";
        String name = line.getName();
        String color = line.getColor();
        jdbcTemplate.update((Connection conn) -> {
            PreparedStatement pstmt = conn.prepareStatement(sql, new String[] {"id"});
            pstmt.setString(1, name);
            pstmt.setString(2, color);
            return pstmt;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return getLine(id);
    }

    private Line getLine(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public boolean existByName(String name) {
        String sql = "SELECT EXISTS(SELECT id FROM LINE WHERE name = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    public List<Line> findAll() {
        String sql = "SELECT id, name, color FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line find(Long id) {
        String sql = "SELECT id, name, color FROM LINE WHERE id =?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public boolean existById(Long id) {
        String sql = "SELECT EXISTS(SELECT * FROM LINE WHERE id = ?) AS SUCCESS";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public void update(Line line) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id =?";
        jdbcTemplate.update(sql, id);
    }
}
