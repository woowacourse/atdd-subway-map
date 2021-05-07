package wooteco.subway.line;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class LineDao {

    public static final RowMapper<LineEntity> LINE_ROW_MAPPER = (resultSet, rowNum) -> new LineEntity(
        resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("color"));
    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LineEntity save(LineEntity lineEntity) {
        String sql = "INSERT INTO line (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, lineEntity.getName());
            ps.setString(2, lineEntity.getColor());
            return ps;
        }, keyHolder);

        return new LineEntity(Objects.requireNonNull(keyHolder.getKey()).longValue(),
            lineEntity.getName(), lineEntity.getColor());
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
        int updatedRowCount = jdbcTemplate.update(sql, lineEntity.getName(), lineEntity.getColor(), id);

        if (updatedRowCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 id 입니다.");
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = (?)";
        int updatedRowCount = jdbcTemplate.update(sql, id);

        if (updatedRowCount == 0) {
            throw new IllegalArgumentException("존재하지 않는 id 입니다.");
        }
    }
}
