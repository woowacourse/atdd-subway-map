package wooteco.subway.line.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineColor;
import wooteco.subway.line.domain.LineName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InMemoryLineDao implements LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        Line persistLine = setId(line);
        lines.add(persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("ID에 해당하는 노선이 없습니다. ID : %d", id)));
    }

    @Override
    public boolean checkExistName(LineName name) {
        return lines.stream()
                .anyMatch(line -> line.getName().equals(name));
    }

    @Override
    public boolean checkExistColor(LineColor color) {
        return lines.stream()
                .anyMatch(line -> line.getColor().equals(color));
    }

    @Override
    public boolean checkExistId(Long id) {
        return lines.stream()
                .anyMatch(line -> line.getId().equals(id));
    }

    private Line setId(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public void update(Line line) {
        lines.set(lines.indexOf(line), line);
    }

    @Override
    public void delete(Long lineId) {
        lines.remove(findById(lineId));
    }
}
