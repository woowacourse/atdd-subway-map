package wooteco.subway.dao;

import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> new Line(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    public JdbcLineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
            .withTableName("LINE")
            .usingGeneratedKeyColumns("id");
    }

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
        try {
            return jdbcTemplate.queryForObject(sql, lineRowMapper, lineId);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchElementException("존재하지 않는 노선입니다.");
        }
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

    @Override
    public boolean existByNameExceptSameId(Long lineId, Line line) {
        String sql = "select exists (select * from LINE where name = (?) and not id = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getName(), lineId);
    }

    @Override
    public boolean existByColorExceptSameId(Long lineId, Line line) {
        String sql = "select exists (select * from LINE where color = (?) and not id = (?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, line.getColor(), lineId);
    }
}
