package wooteco.subway.line.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Line> rowMapper;

    public LineDao(final JdbcTemplate jdbcTemplate, final DataSource source) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(source)
            .withTableName("LINE")
            .usingGeneratedKeyColumns("id");
        this.rowMapper = (rs, rowNum) -> {
            final Long foundId = rs.getLong("id");
            final String name = rs.getString("name");
            final String color = rs.getString("color");
            return new Line(foundId, name, color);
        };
    }

    public Line save(final Line line) {
        Map<String, String> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        long key = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Line(key, line.getName(), line.getColor());
    }

    public Optional<Line> show(final Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
    }

    public List<Line> showAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int countByName(String name) {
        String statement = "SELECT * FROM LINE WHERE name = ?";
        List<Line> stations =  jdbcTemplate.query(statement, rowMapper, name);
        return stations.size();
    }

    public int countByColor(String color) {
        String statement = "SELECT * FROM LINE WHERE color = ?";
        List<Line> stations =  jdbcTemplate.query(statement, rowMapper, color);
        return stations.size();
    }

    public int update(final long id, final Line line) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public int delete(final long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
