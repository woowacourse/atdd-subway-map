package wooteco.subway.dao;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.dto.LineUpdateDto;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LineRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(final Line line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, rowMapper());
    }

    public Line findById(final Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
    }

    public Line findByName(final String name) {
        String sql = "SELECT * FROM line WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            return new Line(id, name, color);
        };
    }

    public void update(final LineUpdateDto lineUpdateDto) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource nameParameters = new BeanPropertySqlParameterSource(lineUpdateDto);
        namedParameterJdbcTemplate.update(sql, nameParameters);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
