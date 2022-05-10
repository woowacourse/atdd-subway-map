package wooteco.subway.dao;

import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.LineEntity;

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

    public LineEntity save(LineEntity lineEntity) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(lineEntity);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new LineEntity(id, lineEntity.getName(), lineEntity.getColor());
    }

    public List<LineEntity> findAll() {
        String SQL = "select * from line;";
        return jdbcTemplate.query(SQL, rowMapper());
    }

    private RowMapper<LineEntity> rowMapper() {
        return (rs, rowNum) -> {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String color = rs.getString("color");
            return new LineEntity(id, name, color);
        };
    }

    public Optional<LineEntity> findById(Long id) {
        String SQL = "select * from line where id = ?;";
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL, rowMapper(), id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(LineEntity lineEntity) {
        String SQL = "update line set name = ?, color = ? where id = ?;";
        jdbcTemplate.update(SQL, lineEntity.getName(), lineEntity.getColor(), lineEntity.getId());
    }

    public void delete(Long id) {
        String SQL = "delete from line where id = ?";
        jdbcTemplate.update(SQL, id);
    }
}
