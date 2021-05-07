package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.exception.NoSuchLineException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao implements LineRepository {
    private Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        this.lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchLineException(1));
    }

    @Override
    public Optional<Line> findByName(String name) {
        return lines.stream()
                .filter(line -> line.getName().equals(name))
                .findAny();
    }

    @Override
    public Line update(Long id, Line newLine) {
        delete(id);
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, newLine, id);
        lines.add(newLine);
        return newLine;
    }

    @Override
    public void delete(Long id) {
        if (!lines.removeIf(line -> line.getId().equals(id))) {
            throw new NoSuchLineException(1);
        }
    }
}
