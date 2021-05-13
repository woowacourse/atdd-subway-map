package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.entity.LineEntity;

@Repository
public class LineDao {

    public static final RowMapper<LineEntity> LINE_ROW_MAPPER = (rs, rowNum) -> new LineEntity(
        rs.getLong("id"), rs.getString("name"), rs.getString("color"));
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LineEntity save(Line line) {
        String sql = "INSERT INTO line (name, color) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return new LineEntity(Objects.requireNonNull(keyHolder.getKey()).longValue(),
            line.getName(), line.getColor());
    }

    public List<LineEntity> findAll() {
        String sql = "SELECT * FROM line";

        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public LineEntity findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = (?)";
        return jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id);
    }

    public void updateById(Long id, LineEntity lineEntity) {
        String sql = "UPDATE line SET name = (?), color = (?) WHERE id = (?) ";
        jdbcTemplate.update(sql, lineEntity.getName(), lineEntity.getColor(), id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = (?)";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM line WHERE id = (?)";

        return jdbcTemplate.queryForObject(sql, Integer.class, id) > 0;
    }

    public boolean existsByNameOrColor(String name, String color) {
        String sql = "SELECT COUNT(*) FROM line WHERE name = (?) OR color = (?)";

        return jdbcTemplate.queryForObject(sql, Integer.class, name, color) > 0;
    }

    public boolean existsByNameAndColorExceptId(Long id, String name, String color) {
        String sql = "SELECT COUNT(*) FROM line WHERE (name = (?) OR color = (?)) AND id <> (?)";

        return jdbcTemplate.queryForObject(sql, Integer.class, name, color, id) > 0;
    }
}
