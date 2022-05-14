package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;

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
    public LineDto save(LineDto lineDto) {
        Long id = insertActor.executeAndReturnKey(
                Map.of("name", lineDto.getName(), "color", lineDto.getColor())).longValue();
        return findById(id);
    }

    @Override
    public List<LineDto> findAll() {
        String sql = "select * from LINE";
        return jdbcTemplate.query(sql, generateMapper());
    }

    @Override
    public LineDto findById(Long id) {
        String sql = "select * from LINE where id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), generateMapper());
    }

    private RowMapper<LineDto> generateMapper() {
        return (resultSet, rowNum) ->
                new LineDto(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color")
                );
    }

    @Override
    public LineDto update(LineDto lineDto) {
        String sql = "update LINE set name = :name, color = :color where id = :id";
        jdbcTemplate.update(sql,
                Map.of("id", lineDto.getId(), "name", lineDto.getName(), "color", lineDto.getColor()));

        return findById(lineDto.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from LINE where id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
