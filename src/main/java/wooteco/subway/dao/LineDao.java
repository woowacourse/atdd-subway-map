package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private final static List<Line> lines = new ArrayList<>();
    private static Long seq = 0L;

    public static Line save(Line line) {
        validateDuplicateName(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);

        return persistLine;
    }

    private static void validateDuplicateName(Line line) {
        lines.stream()
                .filter(it -> it.isSameLine(line))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException(String.format("%s은 이미 존재하는 지하철 노선입니다.", it.getName()));
                });
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선 ID입니다."));
    }

    public static void updateById(Long id, Line line) {
        Line oldLine = findById(id);
        Line newLine = new Line(id, line.getName(), line.getColor());
        lines.remove(oldLine);
        lines.add(newLine);
    }

    public static void deleteById(Long id) {
        lines.remove(findById(id));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);

        return line;
    }
}
