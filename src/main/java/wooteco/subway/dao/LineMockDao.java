package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineMockDao implements LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    @Override
    public long save(final Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine.getId();
    }

    @Override
    public boolean existLineById(final Long id) {
        List<Long> lineNames = lines.stream()
                .map(Line::getId)
                .collect(Collectors.toList());
        return lineNames.contains(id);
    }

    @Override
    public boolean existLineByName(final String name) {
        List<String> lineNames = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        return lineNames.contains(name);
    }

    @Override
    public boolean existLineByColor(final String color) {
        List<String> lineColors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());
        return lineColors.contains(color);
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Optional<Line> find(final Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst();
    }

    @Override
    public void update(final long id, final Line line) {
        delete(id);
        lines.add(line);
    }

    @Override
    public void delete(final Long id) {
        lines.removeIf(line -> line.getId().equals(id));
    }

    public void clear() {
        lines.clear();
    }

    private Line createNewObject(final Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
