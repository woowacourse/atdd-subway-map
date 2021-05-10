package wooteco.subway.line.service.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Line> lineRowMapper;
    private final ObjectMapper objectMapper;

    public JdbcLineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;

        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE").usingGeneratedKeyColumns("id");

        this.lineRowMapper = (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            final String color = rs.getString("color");
            final String name = rs.getString("name");
            return new Line(foundId, name, color);
        };

        objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<Line> findByName(String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        return jdbcTemplate.query(sql, lineRowMapper, name)
                .stream()
                .findAny();
    }

    @Override
    public Line save(Line line) {
        Map<String, String> parameters = objectMapper.convertValue(line, Map.class);

        final long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    @Override
    public Optional<Line> findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.query(sql, lineRowMapper, id).stream().findAny();
    }

    @Override
    public void remove(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void removeAll() {
        String sql = "DELETE FROM line";
        jdbcTemplate.update(sql);
    }

    @Override
    public void update(Long id, String name, String color) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }
}
