package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public JdbcLineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
            .withTableName("LINE")
            .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
        return line;
    };

    @Override
    public Long save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    @Override
    public List<Line> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    @Override
    public void deleteById(Long lineId) {
        String sql = "delete from LINE where id = (?)";
        jdbcTemplate.update(sql, lineId);
    }

    @Override
    public Line findById(Long lineId) {
        String sql = "select * from LINE where id = (?)";

        return jdbcTemplate.queryForObject(sql, lineRowMapper, lineId);
    }

    @Override
    public void update(Long lineId, Line line) {
        String sql = "update LINE set name = (?), color = (?) where id = (?)";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), lineId);
    }

    @Override
    public boolean existByName(Line line) {
        String sql = "select exists (select * from LINE where name = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getName());
    }

    @Override
    public boolean existByColor(Line line) {
        String sql = "select exists (select * from LINE where color = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getColor());
    }
}
