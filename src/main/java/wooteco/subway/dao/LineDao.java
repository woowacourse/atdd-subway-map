package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateNotDuplicated(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateNotDuplicated(Line line) {
        if (lines.stream()
                .anyMatch(persistLine -> persistLine.isDuplicated(line))) {
            throw new DuplicateKeyException("이미 존재하는 노선입니다.");
        }
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(persistLine -> persistLine.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가지는 노선이 존재하지 않습니다."));
    }

    public static void updateById(Long id, Line line) {
        Line targetLine = lines.stream()
                .filter(persistLine -> persistLine.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가지는 노선이 존재하지 않습니다."));
        updateObject(targetLine, line);
    }

    private static void updateObject(Line targetLine, Line line) {
        Field name = ReflectionUtils.findField(Line.class, "name");
        Field color = ReflectionUtils.findField(Line.class, "color");
        name.setAccessible(true);
        color.setAccessible(true);
        ReflectionUtils.setField(name, targetLine, line.getName());
        ReflectionUtils.setField(color, targetLine, line.getColor());
    }
}
