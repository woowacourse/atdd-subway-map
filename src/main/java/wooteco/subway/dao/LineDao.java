package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
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

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.checkId(id))
                .findAny()
                .orElseThrow();
    }

    public static void update(Long id, LineRequest lineRequest) {
        deleteById(id);
        createNewObject(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public static void deleteById(Long id) {
        lines.removeIf(line -> line.checkId(id));
    }
}
