package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Line save(Line line) {
        Map<String, String> params = Map.of("name", line.getName(), "color", line.getColor());
        long savedId = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Line(savedId, line.getName(), line.getColor());
    }

    public Optional<Line> findById(Long id) {
        String sql = "select * from line where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        try {
            Line line = jdbcTemplate.queryForObject(sql, namedParameters, rowMapper());
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Line> findByName(String name) {
        String sql = "select * from line where name = :name";
        SqlParameterSource namedParameters = new MapSqlParameterSource("name", name);
        try {
            Line line = jdbcTemplate.queryForObject(sql, namedParameters, rowMapper());
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public void update(Long targetId, Line newLine) {
        String sql = "update line set name = :name, color = :color where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", newLine.getName())
                .addValue("color", newLine.getColor())
                .addValue("id", targetId);

        jdbcTemplate.update(sql, namedParameters);
    }

    public void delete(Line line) {
        String sql = "delete from line where id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", line.getId());
        jdbcTemplate.update(sql, namedParameters);
    }

    private RowMapper<Line> rowMapper() {
        return (rs, rowNum) ->
                new Line(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("color"));
    }
}
