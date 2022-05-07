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
public class LineDaoImpl implements LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public LineDaoImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Line> ACTOR_ROW_MAPPER = (resultSet, rowNum) -> new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
    );

    @Override
    public Line save(Line line) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        Long id = insertActor.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    @Override
    public List<Line> findAll() {
        String sql = "select * from line";
        return namedParameterJdbcTemplate.query(sql, ACTOR_ROW_MAPPER);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from line where id = :id";
        SqlParameterSource namedParameter = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.update(sql, namedParameter);
    }

    @Override
    public Line findById(Long id) {
        String sql = "select * from line where id = :id";
        SqlParameterSource namedParameter = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameter, ACTOR_ROW_MAPPER);
    }

    @Override
    public boolean existsByNameOrColor(Line line) {
        String sql = "select exists (select name from line where name = :name or color = :color)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(line);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Boolean.class));
    }

    @Override
    public int update(Line updatingLine) {
        String sql = "update line set name = :name, color = :color where id = :id";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(updatingLine);
        return namedParameterJdbcTemplate.update(sql, namedParameters);
    }
}
