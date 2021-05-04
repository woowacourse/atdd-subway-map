package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineDao {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE_ID = "Id에 해당하는 노선이 없습니다.";
    private static final long AFFECTED_ROWS_COUNT = 1;

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(String name, String color) {
        Line line = new Line(++seq, name, color);
        lines.add(line);
        return line;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void deleteAll() {
        lines.clear();
    }

    public static Optional<Line> findById(Long lineId) {
        return lines.stream()
                .filter(line -> line.getId().equals(lineId))
                .findFirst();
    }

    public static Long edit(Long lineId, String color, String name) {
        Line foundLine = findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
        int index = lines.indexOf(foundLine);
        lines.set(index, new Line(lineId, color, name));
        return AFFECTED_ROWS_COUNT;
    }

    public static Long deleteById(Long lineId) {
        Line foundLine = findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
        lines.remove(foundLine);
        return AFFECTED_ROWS_COUNT;
    }
}
