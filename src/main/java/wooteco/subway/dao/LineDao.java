package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {
    private static Long seq = 0L;
    //private static final List<Line> lines = new ArrayList<>();
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        checkDuplication(line.getName());
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void checkDuplication(String name) {
        if (lines.stream().anyMatch(line -> name.equals(line.getName()))) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }

    public static List<Line> findAll() {
        return lines;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> id.equals(line.getId()))
                .findFirst();
    }

    public static void edit(Long id, String name, String color) {
        Line line = findById(id).get();
        lines.remove(line);
        lines.add(new Line(id, name, color));
    }

    public static void clearAll() {
        lines.clear();
        seq = 0L;
    }
}
