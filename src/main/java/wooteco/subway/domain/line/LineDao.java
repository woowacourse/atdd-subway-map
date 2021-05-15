package wooteco.subway.domain.line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.web.exception.SubwayHttpException;

@Repository
public class LineDao {

    private static final String LINE = "line";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COLOR = "color";

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
        } catch (Exception e) {
            throw new SubwayHttpException("중복된 노선 이름입니다");
        }
    }

    public List<Line> findAll() {
        final String sql = "SELECT id, name, color FROM line";
        return namedParameterJdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    // todo 단일조회 예외처리 이슈
    public Line findById(Long id) {
        final String sql = "SELECT id, name, color FROM line WHERE id = :id";

        final MapSqlParameterSource params = getParamSource();
        params.addValue(ID, id);

        return namedParameterJdbcTemplate.queryForObject(sql, params, LINE_ROW_MAPPER);
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
