package wooteco.subway.dao;

import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDaoImpl implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> rowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    public LineDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Line insert(Line line) {
        String name = line.getName();
        String color = line.getColor();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("color", color);

        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Line(id, name, color);
    }

    @Override
    public Line findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public Boolean existByName(Line line) {
        String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getName());
    }

    @Override
    public Boolean existByColor(Line line) {
        String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE color = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return Collections.unmodifiableList(jdbcTemplate.query(sql, rowMapper));
    }

    @Override
    public void update(Long id, String name, String color) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id, id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
