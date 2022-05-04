package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public static List<Line> findAll() {
        return new ArrayList<>(lines);
    }

    public static Line findById(Long lineId) {
        return lines.stream()
                .filter(line -> line.isSameId(lineId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디를 가진 노선이 존재하지 않습니다."));
    }

    public static boolean delete(Long lineId) {
        return lines.removeIf(line -> line.isSameId(lineId));
    }

    public static void update(Long lineId, String lineName, String color) {
        Line byId = findById(lineId);

        Field field = ReflectionUtils.findField(Line.class, "name");
        field.setAccessible(true);
        ReflectionUtils.setField(field, byId, lineName);

        field = ReflectionUtils.findField(Line.class, "color");
        field.setAccessible(true);
        ReflectionUtils.setField(field, byId, color);
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static void deleteAll() {
        lines.clear();
    }
}
