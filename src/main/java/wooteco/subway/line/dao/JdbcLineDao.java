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
    public Line create(Line line) {
        String createSql = "INSERT INTO line (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(createSql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public boolean existByNameAndColor(String name, String color) {
        String countMatchNameOrColorSql = "SELECT count(id) FROM line WHERE name = ? OR color = ?";
        int count = jdbcTemplate.queryForObject(countMatchNameOrColorSql, int.class, name, color);
        return count >= 1;
    }

    @Override
    public List<Line> showAll() {
        String showAllSql = "SELECT * FROM line";

        return jdbcTemplate.query(showAllSql, lineRowMapper());
    }

    @Override
    public Line findById(Long lineId) {
        String showMatchIdSql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(showMatchIdSql, lineRowMapper(), lineId);
    }

    @Override
    public boolean existById(Long lineId) {
        String countMatchIdSql = "SELECT count(id) FROM line WHERE id = ?";
        int count = jdbcTemplate.queryForObject(countMatchIdSql, int.class, lineId);
        return count >= 1;
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String color = rs.getString("color");
            return new Line(id, name, color);
        };
    }
}
