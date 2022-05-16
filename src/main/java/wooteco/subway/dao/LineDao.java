package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private static final RowMapper<Line> LINE_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public LineDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = simpleInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, LINE_MAPPER);
    }

    public Optional<Line> findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, parameters, LINE_MAPPER)
                .stream()
                .findAny();
    }

    public void updateById(Long id, Line line) {
        String sql = "UPDATE LINE SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id)
                .addValue("name", line.getName())
                .addValue("color", line.getColor());
        jdbcTemplate.update(sql, parameters);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM LINE WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameters);
    }

    public boolean existsId(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM LINE WHERE id = :id)";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

    public boolean existsName(Line line) {
        String sql = "SELECT EXISTS(SELECT 1 FROM LINE WHERE name = :name AND id != :id)";
        SqlParameterSource parameters = decideParametersForExists(line);
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

    public boolean existsColor(Line line) {
        String sql = "SELECT EXISTS(SELECT 1 FROM LINE WHERE color = :color AND id != :id)";
        SqlParameterSource parameters = decideParametersForExists(line);
        return Integer.valueOf(1).equals(jdbcTemplate.queryForObject(sql, parameters, Integer.class));
    }

    private SqlParameterSource decideParametersForExists(Line line) {
        if (line.getId() == null) {
            return new BeanPropertySqlParameterSource(new Line(0L, line.getName(), line.getColor()));
        }
        return new BeanPropertySqlParameterSource(line);
    }

}
