package wooteco.subway.dao.line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<Line> mapper = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(id, name, color);
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Line save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM Line";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Optional<Line> findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.query(sql, mapper, id)
            .stream().findAny();
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT EXISTS (SELECT * FROM line WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT EXISTS (SELECT * FROM line WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public void update(Line updatedLine) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate
            .update(sql, updatedLine.getName(), updatedLine.getColor(), updatedLine.getId());
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
