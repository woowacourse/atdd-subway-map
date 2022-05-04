package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    public static final String TABLE_NAME = "LINE";
    public static final String KEY_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public JdbcLineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    @Override
    public Line save(Line line) {
        Long id = insertActor.executeAndReturnKey(
                Map.of("name", line.getName(), "color", line.getColor())).longValue();
        return findById(id);
    }

    @Override
    public List<Line> findAll() {
        return null;
    }

    @Override
    public Line findById(Long id) {
        String sql = "select * from LINE where id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), generateMapper());
    }

    private RowMapper<Line> generateMapper() {
        return (resultSet, rowNum) ->
                new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                );
    }

    @Override
    public Line update(Long id, Line updateLine) {
        String sql = "update LINE set name = :name, color = :color where id = :id";
        jdbcTemplate.update(sql,
                Map.of("id", id, "name", updateLine.getName(), "color", updateLine.getColor()));

        return findById(id);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteAll() {

    }
}
