package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine  = createNewObject(line);
        add(persistLine);
        return persistLine;
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

    public static Optional<Line> findById(long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst();
    }

    public static boolean add(Line newLine) {
        return lines.add(newLine);
    }

    public static boolean delete(Line line) {
        return lines.remove(line);
    }
}
