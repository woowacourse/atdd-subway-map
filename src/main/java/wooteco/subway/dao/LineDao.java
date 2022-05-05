package wooteco.subway.dao;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color")
            );

    public Line insert(String name, String color) {
        Long id = simpleJdbcInsert.executeAndReturnKey(Map.of("name", name, "color", color)).longValue();
        return new Line(id, name, color);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineRowMapper, id));
    }

    public int delete(Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int update(Long id, String name, String color) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        return jdbcTemplate.update(sql, name, color, id);
    }

    public boolean isExistId(Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    public boolean isExistName(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, name));
    }

    public boolean isExistName(Long id, String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id != ? AND name = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id, name));
    }

}
