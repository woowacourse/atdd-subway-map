package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line saveLine(Line line) {
        Line savedLine = createNewObject(line);
        lines.add(savedLine);
        return savedLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        if (field != null) {
            field.setAccessible(true);
            ReflectionUtils.setField(field, line, ++seq);
        }
        return line;
    }

    public static List<Line> findAllLines() {
        return new ArrayList<>(lines);
    }

    public static void deleteAllLines() {
        lines.clear();
    }
}
