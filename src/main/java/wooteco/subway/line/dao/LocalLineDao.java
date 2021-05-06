package wooteco.subway.line.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class LocalLineDao implements LineDao {

    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<Line> findByName(String lineName) {
        return lines.stream()
                .filter(line -> line.getName().equals(lineName))
                .findAny();
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public void delete(Long id) {
        lines.removeIf(line -> line.getId().equals(id));
    }

    @Override
    public void update(Line newLine) {
        lines.stream()
                .filter(line -> line.getId().equals(newLine.getId()))
                .map(line -> updateObject(line, newLine))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("수정할 대상이 없습니다."));
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    private Line updateObject(Line line, Line updateLine) {
        Field color = ReflectionUtils.findField(Line.class, "color");
        Field name = ReflectionUtils.findField(Line.class, "name");

        color.setAccessible(true);
        name.setAccessible(true);

        ReflectionUtils.setField(color, line, updateLine.getColor());
        ReflectionUtils.setField(name, line, updateLine.getName());
        return line;
    }
}
