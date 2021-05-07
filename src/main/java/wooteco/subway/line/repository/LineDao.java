package wooteco.subway.line.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private static final int INDEX_MATCHER = 1;
    public static final long INITIAL_INDEX = 0L;
    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(final Line line) {
        Line persistLine = createNewObject(line);
        if (lines.contains(persistLine)) {
            throw new IllegalArgumentException("이미 존재하는 노선 입니다.");
        }
        lines.add(persistLine);
        return persistLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static void deleteAll() {
        seq = INITIAL_INDEX;
        lines.clear();
    }

    public static List<Line> getLines() {
        return new ArrayList<>(lines);
    }

    public static Line getLine(final Long id) {
        return lines.get(getIndexById(id));
    }

    public static void updateLine(final Long id, final Line line) {
        lines.set(getIndexById(id), line);
    }

    public static void deleteById(final Long id) {
        lines.remove(getIndexById(id));
    }

    private static int getIndexById(final Long id) {
        return id.intValue() - INDEX_MATCHER;
    }
}
