package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@Repository
public class LineDao {

    private static final int NO_ROW_AFFECTED = 0;
    private static final String LINE_DUPLICATED = "이미 존재하는 노선입니다. ";
    private static final String LINE_NOT_FOUND = "요청한 노선이 존재하지 않습니다. ";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public LineDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(final Line line) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());
        try {
            final Long id = simpleInsert.executeAndReturnKey(params).longValue();
            return new Line(id, line.getName(), line.getColor());
        } catch (DuplicateKeyException e) {
            throw new IllegalStateException(LINE_DUPLICATED + line);
        }
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from LINE";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Line(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("color"));
        });
    }

    public Line findById(final Long id) {
        final String sql = "select id, name, color from LINE where id = :id";
        final SqlParameterSource parameter = new MapSqlParameterSource(Map.of("id", id));
        return namedParameterJdbcTemplate.queryForObject(sql, parameter, (resultSet, rowNum) -> {
            return new Line(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("color"));
        });
    }

    public void update(final Long id, final Line line) {
        final String sql = "update LINE set name = :name, color = :color where id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());
        params.put("id", id);
        final SqlParameterSource parameter = new MapSqlParameterSource(params);
        final int theNumberOfAffectedRow = namedParameterJdbcTemplate.update(sql, parameter);
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(LINE_NOT_FOUND + "id=" + id + " " + line);
        }
    }

    public void deleteById(final Long id) {
        final String sql = "delete from LINE where id = :id";
        final int theNumberOfAffectedRow = namedParameterJdbcTemplate.update(sql, Map.of("id", id));
        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
            throw new IllegalStateException(LINE_NOT_FOUND + "id=" + id);
        }
    }

}
