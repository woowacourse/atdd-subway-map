package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao{

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    @Override
    public Line insert(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 노선은 존재하지 않습니다."));
    }

    @Override
    public List<String> findNames() {
        return lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findColors() {
        return lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public void update(Long id, String name, String color) {
        delete(id);
        lines.add(new Line(id, name, color));
    }

    @Override
    public void delete(Long id) {
        Line line = findById(id);
        lines.remove(line);
    }
}
