package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();
    private static LineDao instance;

    private LineDao() {
    }

    public static LineDao getInstance() {
        if (instance == null) {
            instance = new LineDao();
        }

        return instance;
    }

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

    public List<Line> findAll() {
        return lines;
    }

    public void update(Long id, String name, String color) {
        Line line = findById(id);
        line = new Line(id, name, color);
    }

    public Line findById(Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny()
            .orElseThrow(NoLineException::new);
    }

    public void delete(Long id) {
        lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny()
            .ifPresent(line -> lines.remove(line));
    }
}
