package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    public static List<Line> lines = new ArrayList<>();

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
}
