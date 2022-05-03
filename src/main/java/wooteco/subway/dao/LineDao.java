package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    public static final String DUPLICATE_LINE_NAME = "[ERROR] 중복된 노선 이름이 있습니다.";
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    private LineDao() {}

    public static Line save(Line line) {
        validateDuplicatedInSave(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateDuplicatedInSave(Line line) {
        if (isDuplicatedName(line)) {
            throw new IllegalArgumentException(DUPLICATE_LINE_NAME);
        }
    }

    private static boolean isDuplicatedName(Line line) {
        return lines.stream().map(Line::getName)
                .anyMatch(name -> line.getName().equals(name));
    }

    private static boolean isDuplicatedNameWithDifferentId(Long id, String newName) {
        return lines.stream().anyMatch(eachLine -> id != eachLine.getId() && newName.equals(eachLine.getName()));
    }

    private static void validateDuplicatedInPut(Long id, String newName) {
        if (isDuplicatedNameWithDifferentId(id, newName)) {
            throw new IllegalArgumentException();
        }
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static void deleteById(Long id) {
        lines.removeIf(it -> it.getId().equals(id));
    }

    public static Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    public static void changeLineName(Long id, String newName) {
        Optional<Line> findLine = findById(id);
        validateIdExist(findLine);
        Line line = findLine.get();
        validateDuplicatedInPut(id, newName);
        line.setName(newName);
    }

    private static void validateIdExist(Optional<Line> resultById) {
        if (resultById.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 등록되지 않은 ID입니다.");
        }
    }



    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
