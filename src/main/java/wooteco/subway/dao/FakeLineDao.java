package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class FakeLineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        boolean existName = lines.stream()
                .anyMatch(line::isSameName);
        if (existName) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
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
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 id입니다."));
    }

    public static Line update(Long id, Line updateLine) {
        lines.remove(findById(id));

        Line line = new Line(id, updateLine.getName(), updateLine.getColor());
        lines.add(line);

        return line;
    }

    public static void deleteById(Long id) {
        lines.remove(findById(id));
    }

    public void deleteAll() {
        seq = 0L;
        lines = new ArrayList<>();
    }
}
