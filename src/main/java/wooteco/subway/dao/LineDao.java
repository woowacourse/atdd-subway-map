package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        validateLineName(line);

        String sql = "INSERT INTO line (name, color) VALUES (:name, :color)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);

        long lineId = keyHolder.getKey().longValue();

        return new Line(lineId, line.getName(), line.getColor());
    }

    private void validateLineName(Line line) {
        String sql = "SELECT id, name, color FROM line WHERE name = :name";

        Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        List<Line> lines = jdbcTemplate.query(sql, params,
                (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));

        if (lines.size() > 0) {
            throw new IllegalArgumentException("같은 이름의 노선은 등록할 수 없습니다.");
        }
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(),
                (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));
    }

    public Line findById(Long id) {
        String sql = "SELECT id, name, color FROM line WHERE id=:id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        List<Line> lines = jdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) -> new Line(rs.getLong("id"), rs.getString("name"), rs.getString("color")));

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
        return lines.get(0);
    }

    public Line update(Long id, String name, String color) {
        String sql = "UPDATE line SET name=:name, color=:color where id=:id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);
        params.put("color", color);

        jdbcTemplate.update(sql, params);

        return new Line(id, name, color);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id=:id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        int affected = jdbcTemplate.update(sql, params);

        if (affected == 0) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }
}
