package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicatedNameException;

@Repository
public class LineDao {

    private static final String LINE = "line";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COLOR = "color";
    private static final String LINE_RESOURCE_NAME = "노선";

    private static final RowMapper<Line> LINE_ROW_MAPPER = (rs, rowNum) ->
            new Line(
                    rs.getLong(ID),
                    rs.getString(NAME),
                    rs.getString(COLOR)
            );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(LINE)
                .usingGeneratedKeyColumns(ID);
    }

    public Long save(Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME, line.getName());
        params.put(COLOR, line.getColor());

        try {
            return simpleJdbcInsert.executeAndReturnKey(params).longValue();
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException(LINE_RESOURCE_NAME);
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT id, name, color FROM line";
        return namedParameterJdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    public Optional<Line> findById(Long id) {
        final String sql = "SELECT id, name, color FROM line WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(ID, id);

        try {
            Line line = namedParameterJdbcTemplate.queryForObject(sql, params, LINE_ROW_MAPPER);
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(Long id, Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(NAME, line.getName());
        params.addValue(COLOR, line.getColor());
        params.addValue(ID, id);

        namedParameterJdbcTemplate.update(sql, params);
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(ID, id);

        namedParameterJdbcTemplate.update(sql, params);
    }

    private MapSqlParameterSource getParamSource() {
        return new MapSqlParameterSource();
    }
}
