package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.NoLineFoundException;

public class LineDao {
    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplicatedLine(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateDuplicatedLine(Line line) {
        if (lines.contains(line)) {
            throw new LineDuplicateException();
        }
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static List<Line> findAll() {
        return new ArrayList<>(lines);
    }

    public static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny()
                .orElseThrow(NoLineFoundException::new);
    }

    public static void deleteById(Long id) {
        lines.remove(findById(id));
    }

    public static void deleteAll() {
        lines.removeAll(new ArrayList<>(lines));
    }

    public static void update(Long id, LineRequest lineRequest) {
        lines.remove(findById(id));
        lines.add(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }
}
