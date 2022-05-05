package wooteco.subway.dao;

import java.util.List;
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
    private static final RowMapper<Line> lineMapper = (resultSet, rowNum) -> new Line(
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
        return jdbcTemplate.query(sql, lineMapper);
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM LINE WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(sql, parameters, lineMapper);
    }

    public void updateById(Line line) {
        String sql = "UPDATE LINE SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        jdbcTemplate.update(sql, parameters);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM LINE WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameters);
    }
}
