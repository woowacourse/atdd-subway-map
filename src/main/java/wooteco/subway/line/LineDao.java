package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.NameDuplicationException;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        if (lines.contains(line)) {
            throw new NameDuplicationException("중복된 이름입니다.");
        }

        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void delete(Long id) {
        lines.stream()
            .filter(line -> line.getId().equals(id))
            .findFirst()
            .ifPresent(line -> lines.remove(line));
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static Line find(Long id) {
        return lines.stream()
            .filter(line -> line.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("없는 노선입니다."));
    }

    public static void modify(Long id, LineRequest lineRequest) {
        Line line = find(id);
        lines.set(lines.indexOf(line), new Line(line.getId(), lineRequest.getName(),
            lineRequest.getColor()));
    }
}
