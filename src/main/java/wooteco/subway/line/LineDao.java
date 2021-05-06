package wooteco.subway.line;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

@Repository
public class LineDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public long save(Line line) {
        String query = "INSERT INTO line(name, color) VALUES(?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void delete(Long id) {
        lines.stream()
            .filter(line -> line.getId().equals(id))
            .findFirst()
            .ifPresent(line -> lines.remove(line));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Line find(Long id) {
        return lines.stream()
            .filter(line -> line.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("없는 노선입니다."));
    }

    public static void modify(Long id, LineRequest lineRequest) {
        Line line = find(id);
        lines.set(lines.indexOf(line), new Line(line.getId(), lineRequest.getName(),
            lineRequest.getColor()));
    }
}
