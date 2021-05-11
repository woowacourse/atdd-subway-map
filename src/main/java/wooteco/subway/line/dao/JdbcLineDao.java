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
    private static final String CREATE = "INSERT INTO line (name, color) VALUES (?, ?)";
    private static final String SHOW_ALL = "SELECT * FROM line";
    private static final String SHOW_MATCH_ID = "SELECT * FROM line WHERE id = ?";
    private static final String COUNT_MATCH_NAME_OR_COLOR = "SELECT count(id) FROM line WHERE name = ? OR color = ?";
    private static final String COUNT_MATCH_ID = "SELECT count(id) FROM line WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Line create(Line line) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(CREATE, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return Line.create(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public boolean existByInfo(String name, String color) {
        Integer count = jdbcTemplate.queryForObject(COUNT_MATCH_NAME_OR_COLOR, int.class, name, color);
        return count >= 1;
    }

    public List<Line> showAll() {
        return jdbcTemplate.query(SHOW_ALL, lineRowMapper());
    }

    @Override
    public Line findById(Long lineId) {
        return jdbcTemplate.queryForObject(SHOW_MATCH_ID, lineRowMapper(), lineId);
    }

    @Override
    public boolean existById(Long lineId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_MATCH_ID, int.class, lineId);
        return count >= 1;
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String color = rs.getString("color");
            return Line.create(id, name, color);
        };
    }
}
