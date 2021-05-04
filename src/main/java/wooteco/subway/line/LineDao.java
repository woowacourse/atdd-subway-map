package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
}
