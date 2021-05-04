package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LineDao {

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        validateToSave(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private void validateToSave(Line lineToSave) {
        if (hasSameName(lineToSave) || hasSameColor(lineToSave)) {
            throw new IllegalArgumentException("중복된 이름이나 색의 노선을 생성할 수 없습니다.");
        }
    }

    private boolean hasSameName(Line lineToSave) {
        return lines.stream().anyMatch(line -> line.hasSameName(lineToSave));
    }

    private boolean hasSameColor(Line lineToSave) {
        return lines.stream().anyMatch(line -> line.hasSameColor(lineToSave));
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public List<Line> findAll() {
        return new ArrayList<>(lines);
    }

    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id 입니다."));
    }

    public void updateById(Long id, Line line) {
        validateToSave(line);
        List<Line> linesToUpdate = lines.stream()
                .map(persistLine -> persistLine.getId().equals(id) ? line : persistLine)
                .collect(Collectors.toList());
        validateNewLines(linesToUpdate);
        lines = linesToUpdate;
    }

    private void validateNewLines(List<Line> linesToUpdate) {
        if (lines.containsAll(linesToUpdate)) {
            throw new IllegalArgumentException("존재하지 않는 id 입니다.");
        }
    }

    public void deleteById(Long id) {
        boolean isRemoved = lines.removeIf(line -> line.getId().equals(id));

        if (!isRemoved) {
            throw new IllegalArgumentException("존재하지 않는 ID 입니다.");
        }
    }
}
