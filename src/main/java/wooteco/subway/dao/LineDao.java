package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
