package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Line save(Line line) {
        String sql = "insert into LINE (name, color) values (:name, :color)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new Line(Objects.requireNonNull(keyHolder.getKey()).longValue(), line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select * from LINE";

        return namedParameterJdbcTemplate.query(sql,
                (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
    }

    public Optional<Line> findById(Long id) {
        String sql = "select * from LINE where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        List<Line> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) ->
                        new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<Line> findByName(String name) {
        String sql = "select * from LINE where name = :name";

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        List<Line> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) ->
                        new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public int update(Long id, Line newLine) {
        String sql = "update LINE set name = :name, color = :color where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", newLine.getName());
        params.put("color", newLine.getColor());

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public int deleteById(Long id) {
        String sql = "delete from LINE where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
