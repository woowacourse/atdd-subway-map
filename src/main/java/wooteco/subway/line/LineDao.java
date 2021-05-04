package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicationException;

public class LineDao {
    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplicateNameAndColor(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateDuplicateNameAndColor(Line line) {
        if (isDuplicateName(line)) {
            throw new DuplicationException("이미 존재하는 노선 이름입니다.");
        }

        if (isDuplicateColor(line)) {
            throw new DuplicationException("이미 존재하는 노선 색깔입니다.");
        }
    }

    private static boolean isDuplicateColor(Line newLine) {
        return lines.stream()
            .anyMatch(line -> line.isSameColor(newLine)) ;
    }

    private static boolean isDuplicateName(Line newLine) {
        return lines.stream()
            .anyMatch(line -> line.isSameName(newLine)) ;
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

    public static Line findLineById(Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static void update(Line updatedLine) {
        validateDuplicateNameAndColor(updatedLine);

        Integer index = lines.stream()
            .filter(line -> line.isSameId(updatedLine.getId()))
            .map(line -> lines.indexOf(line))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
        lines.set(index, updatedLine);
    }

    public static void deleteLineById(Long id) {
        Integer index = lines.stream()
            .filter(line -> line.isSameId(id))
            .map(line -> lines.indexOf(line))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
        lines.remove(index);
    }

    public static void deleteAll() {
        lines.clear();
    }
}
