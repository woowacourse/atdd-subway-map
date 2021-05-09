package wooteco.subway.line.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existsByName(String name) {
        final String sql = "SELECT count(id) FROM line WHERE name = ?";
        Integer count = this.jdbcTemplate.queryForObject(sql, int.class, name);

        return count >= 1;
    }

    @Override
    public Line save(Line line) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", line.getName());
        parameters.put("color", line.getColor());

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("LINE").usingGeneratedKeyColumns("id");
        final long id = jdbcInsert.executeAndReturnKey(parameters).longValue();

        return Line.of(id, line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper());
    }

    @Override
    public boolean existsById(Long id) {
        final String sql = "SELECT count(id) FROM line WHERE id = ?";
        Integer count = this.jdbcTemplate.queryForObject(sql, int.class, id);

        return count >= 1;
    }

    @Override
    public Line findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper(), id);
    }

    @Override
    public void removeById(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Long id, Line line) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            final String color = rs.getString("color");
            final String name = rs.getString("name");
            return Line.of(foundId, name, color);
        };
    }
}
