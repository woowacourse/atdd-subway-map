package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Line line) {
        final String sql = "insert into LINE (name, color) values (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public static List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    public static void update(Line line) {
        delete(line.getId());
        lines.add(line);
    }

    public static void delete(Long id) {
        boolean isRemoving = lines.removeIf(line -> line.getId().equals(id));
        if (!isRemoving) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }
    }

    public static void clear() {
        lines.clear();
    }
}
