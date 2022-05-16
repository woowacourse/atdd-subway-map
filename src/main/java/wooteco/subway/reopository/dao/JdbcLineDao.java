package wooteco.subway.reopository.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao {

    private static final RowMapper<Line> mapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());
        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public Optional<Line> findById(Long id) {
        String sql = "select * from line where id = (?)";
        return Optional.ofNullable(DataAccessUtils.singleResult(jdbcTemplate.query(sql, mapper, id)));
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, mapper);
    }

    public void modifyById(Long id, Line line) {
        String sql = "update line set name = (?), color = (?) where id = (?)";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    public void deleteById(Long id) {
        String sql = "delete from line where id = (?)";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        String sql = "delete from station";
        jdbcTemplate.update(sql);
    }

    public boolean existByNameAndColor(String name, String color) {
        String sql = "select exists(select * from line where name = (?) and color = (?))";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, name, color);
        return count != 0;
    }
}
