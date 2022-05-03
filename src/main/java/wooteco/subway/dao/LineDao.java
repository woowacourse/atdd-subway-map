package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public boolean existsByName(String name) {
        return lines.stream()
                .map(Line::getName)
                .filter(it -> it.equals(name))
                .count() != 0;
    }

    public void deleteAll() {
        lines = new ArrayList<>();
    }

    public List<Line> findAll() {
        return Collections.unmodifiableList(lines);
    }

    public boolean notExistsById(Long id) {
        return lines.stream()
                .map(Line::getId)
                .filter(it -> it.equals(id))
                .count() == 0;
    }

    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void updateLineById(Long id, String name, String color) {
        System.out.println(lines + "!!!");
        final Line line = findById(id);
        lines.remove(line);
        lines.add(new Line(id, name, color));
        System.out.println(lines);
    }

    public void deleteById(Long id) {
        final Line line = findById(id);
        lines.remove(line);
    }
}
