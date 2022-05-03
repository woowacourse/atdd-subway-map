package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(final Line line) {
        if (isDuplicateName(line)) {
            throw new IllegalArgumentException("중복된 이름의 노선은 저장할 수 없습니다.");
        }

        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static boolean isDuplicateName(final Line line) {
        return lines.stream()
                .anyMatch(it -> it.isSameName(line));
    }

    public static List<Line> findAll() {
        return lines;
    }

    private static Line createNewObject(final Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static void deleteAll() {
        lines.clear();
        seq = 0L;
    }
}
