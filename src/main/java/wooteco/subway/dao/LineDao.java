package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();
    private static final String LINE_VALIDATION = "이미 등록된 노선입니다.";

    public static Line save(Line line) {
        validateDuplication(line);

        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateDuplication(Line line) {
        if (lines.contains(line)) {
            throw new IllegalArgumentException(LINE_VALIDATION);
        }
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
