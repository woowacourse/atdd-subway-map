package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.line.Line;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line Line) {
        Line persistLine = createNewObject(Line);
        lines.add(persistLine);
        return persistLine;
    }

    public List<Line> findAll() {
        return lines;
    }

    public Optional<Line> findById(long id) {
        return lines.stream()
            .filter(Line -> Line.getId() == id)
            .findAny();
    }

    private Line createNewObject(Line Line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, Line, ++seq);
        return Line;
    }

    public void updateLine(Line line, String name, String color) {
        Field nameField = ReflectionUtils.findField(Line.class, "name");
        nameField.setAccessible(true);
        ReflectionUtils.setField(nameField, line, name);
        Field colorField = ReflectionUtils.findField(Line.class, "color");
        colorField.setAccessible(true);
        ReflectionUtils.setField(colorField, line, color);
    }

    public void deleteById(long id) {
        lines.removeIf(Line -> Line.getId() == id);
    }
}
