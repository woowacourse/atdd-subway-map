package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.entity.LineEntity;

@Repository
public class LineDao {

    private static final RowMapper<LineEntity> mapper = (rs, rowNum) ->
        new LineEntity(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
        );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("line")
            .usingGeneratedKeyColumns("id");
    }

    public LineEntity save(LineEntity line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("name", line.getName())
            .addValue("color", line.getColor());
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new LineEntity(id, line.getName(), line.getColor());
    }

    public Optional<LineEntity> findById(Long id) {
        String sql = "select *  from line where id = ?";
        return Optional.ofNullable(DataAccessUtils.singleResult(jdbcTemplate.query(sql, mapper, id)));
    }

    public List<LineEntity> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, mapper);
    }

    public void modifyById(LineEntity line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void deleteById(Long id) {
        String sql = "delete from line where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existByNameAndColor(String name, String color) {
        String sql = "select exists (select * from line where name = ? and color = ? limit 1) as success";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name, color);
    }
}
