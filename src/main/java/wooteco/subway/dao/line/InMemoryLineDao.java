package wooteco.subway.dao.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLineDao implements LineDao {

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Line save(Line line) {
        Line createdLine = createNewObject(line);
        lines.add(createdLine);
        return createdLine;
    }

    @Override
    public Optional<Line> findByNameAndColor(String name, String color) {
        return lines.stream()
                .filter(line -> line.isSameName(name) || line.isSameColor(color))
                .findAny();
    }

    @Override
    public Optional<Line> findById(Long lineId) {
        return lines.stream()
                .filter(line -> line.isSameId(lineId))
                .findAny();
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public void delete(Long id) {
        lines.removeIf(line -> line.isSameId(id));
    }
}
