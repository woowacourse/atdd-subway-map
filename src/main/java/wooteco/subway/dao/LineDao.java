package wooteco.subway.dao;

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

    public Line save(String name, String color) {
        Long id = simpleJdbcInsert.executeAndReturnKey(Map.of("name",name,"color",color)).longValue();
        return new Line(id, name, color);
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";
        Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        if (line == null) {
            new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
        return line;
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    public void update(Long id, String name, String color) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, name, color, id);
        if (count == 0) {
            new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    public void validate(String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = ?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, name);
        if (result) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복될 수 없습니다.");
        }
    }

    public void validate(Long id, String name) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id != ? AND name = ?)";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, id, name);
        if (result) {
            throw new IllegalArgumentException("지하철 노선 이름이 중복될 수 없습니다.");
        }
    }

}
