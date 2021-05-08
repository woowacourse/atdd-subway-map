package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineJavaDao implements LineDao {

    private Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        validateDuplicatedLine(line);
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

    private void validateDuplicatedLine(Line newLine) {
        if (isDuplicatedName(newLine) || isDuplicatedColor(newLine)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicatedName(Line newLine) {
        return lines.stream()
            .anyMatch(line -> line.isSameName(newLine));
    }

    private boolean isDuplicatedColor(Line newLine) {
        return lines.stream()
            .anyMatch(line -> line.isSameColor(newLine));
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public void update(Long id, String name, String color) {
        Line line = findById(id).orElseThrow(NoLineException::new);
        line = new Line(id, name, color);
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny();
    }

    @Override
    public void delete(Long id) {
        lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny()
            .ifPresent(line -> lines.remove(line));
    }

    @Override
    public Line findByName(String name) {
        return null;
    }

    @Override
    public Line findByColor(String color) {
        return null;
    }
}
