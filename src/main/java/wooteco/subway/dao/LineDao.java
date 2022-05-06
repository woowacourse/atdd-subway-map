package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;
import java.util.List;

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

    public Line save(Line line) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String SQL = "select * from line;";
        return jdbcTemplate.query(SQL, rowMapper());
    }

    private RowMapper<Line> rowMapper() {
        return (rs, rowNum) -> {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String color = rs.getString("color");
            return new Line(id, name, color);
        };
    }

    public Line findById(Long id) {
        String SQL = "select * from line where id = ?;";
        return jdbcTemplate.queryForObject(SQL, rowMapper(), id);
    }

    public void update(Line line) {
        findById(line.getId());
        String SQL = "update line set name = ?, color = ? where id = ?;";
        jdbcTemplate.update(SQL, line.getName(), line.getColor(), line.getId());
    }

    public void delete(Long id) {
        findById(id);
        String SQL = "delete from line where id = ?";
        jdbcTemplate.update(SQL, id);
    }
}
