package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;

public class LineDao {

    private final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        validateDuplicateName(line);
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private void validateDuplicateName(Line line) {
        String name = line.getName();
        if (lines.stream().anyMatch(it -> it.isSameName(name))) {
            throw new DuplicateException();
        }
    }

    public List<Line> findAll() {
        return new ArrayList<>(lines);
    }

    public Line findById(Long id) {
        return lines.stream()
            .filter(it -> it.isSameId(id))
            .findFirst()
            .orElseThrow(NotExistItemException::new);
    }

    public Line update(Line newLine) {
        Line line = findById(newLine.getId());

        lines.remove(line);
        lines.add(newLine);
        return newLine;
    }

    public void delete(Long id) {
        Line line = findById(id);

        lines.remove(line);
    }
}
