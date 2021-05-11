package wooteco.subway.line.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return Line.create(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public boolean existByInfo(String name, String color) {
        String sql = "SELECT count(id) FROM line WHERE name = ? OR color = ?";
        Integer count = jdbcTemplate.queryForObject(sql, int.class, name, color);
        return count >= 1;
    }

    public List<Line> showAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper());
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String color = rs.getString("color");
            return Line.create(id, name, color);
        };
    }

    @Override
    public Line findById(Long lineId) {
        String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper(), lineId);
    }

    @Override
    public boolean existById(Long lineId) {
        String sql = "SELECT count(id) FROM line WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, int.class, lineId);
        return count >= 1;
    }
}
