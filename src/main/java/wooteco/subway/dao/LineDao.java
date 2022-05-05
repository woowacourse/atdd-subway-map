package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    protected static final String NAME_DUPLICATE_EXCEPTION_MESSAGE = "이름이 중복된 노선은 만들 수 없습니다.";
    protected static final String COLOR_DUPLICATE_EXCEPTION_MESSAGE = "색깔이 중복된 노선은 만들 수 없습니다.";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> rowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line insert(Line line) {
        String name = line.getName();
        String color = line.getColor();
        validateDuplicateName(name);
        validateDuplicateColor(color);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("color", color);

        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Line(id, name, color);
    }

    private void validateDuplicateName(String name) {
        List<Line> lines = findAll();
        boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameName(name));
        if (isDuplicate) {
            throw new IllegalArgumentException(NAME_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicateColor(String color) {
        List<Line> lines = findAll();
        boolean isDuplicate = lines.stream()
                .anyMatch(line -> line.isSameColor(color));
        if (isDuplicate) {
            throw new IllegalArgumentException(COLOR_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void update(Long id, String name, String color) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
