package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

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

    public static void deleteAll(){
        lines.clear();
    }

    public static List<Line> getLines() {
        return new ArrayList<>(lines);
    }
}
