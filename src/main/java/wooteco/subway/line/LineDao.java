package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

    public static Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny();
    }

    public static void update(Line currentLine, Line updatedLine) {
        IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).equals(currentLine))
                .mapToObj(i -> lines.set(i, updatedLine))
    }
}
