package wooteco.subway.dao;

import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import wooteco.subway.domain.Line;

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

    public Optional<Line> findById(Long id) {
        String SQL = "select * from line where id = ?;";
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL, rowMapper(), id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(Line line) {
        String SQL = "update line set name = ?, color = ? where id = ?;";
        jdbcTemplate.update(SQL, line.getName(), line.getColor(), line.getId());
    }

    public void delete(Long id) {
        String SQL = "delete from line where id = ?";
        jdbcTemplate.update(SQL, id);
    }
}
