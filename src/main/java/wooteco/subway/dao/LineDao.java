package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundException;

public class LineDao {

    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.hasIdOf(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("해당되는 노선은 존재하지 않습니다."));
    }

    public static Line save(Line line) {
        validateUniqueName(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public static void update(Line line) {
        validateUniqueName(line);
       Line currentLine = lines.stream()
                .filter(it -> it.hasIdOf(line.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당되는 노선은 존재하지 않습니다."));

        currentLine.setName(line.getName());
        currentLine.setColor(line.getColor());
    }

    public static void deleteById(Long id) {
        boolean removed = lines.removeIf(it -> it.getId().equals(id));
        if (!removed) {
            throw new IllegalArgumentException("해당되는 노선은 존재하지 않습니다.");
        }
    }

    public static void clear() {
        seq = 0L;
        lines.clear();
    }

    private static void validateUniqueName(Line newLine) {
        boolean hasDuplicate = lines.stream()
                .filter(it -> !it.hasIdOf(newLine.getId()))
                .anyMatch(it -> it.hasNameOf(newLine.getName()));
        if (hasDuplicate) {
            throw new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다.");
        }
    }

    private static Line createNewObject(Line station) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
