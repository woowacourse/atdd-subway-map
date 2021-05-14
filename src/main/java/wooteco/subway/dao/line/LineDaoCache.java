package wooteco.subway.dao.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDaoCache implements LineDao {

    private final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Optional<Line> findLineByName(String name) {
        return lines.stream()
            .filter(line -> line.isSameName(name))
            .findAny();
    }

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Optional<Line> findLineById(Long id) {
        return lines.stream()
            .filter(line -> line.isSameId(id))
            .findAny();
    }

    @Override
    public void removeLine(Long id) {
        lines.removeIf(line -> line.isSameId(id));
    }

    @Override
    public void update(Line line) {
        findLineById(line.getId()).get().changeInfo(line.getName(), line.getColor());
    }

    @Override
    public Optional<Line> findLineByNameOrColor(String name, String color, Long lineId) {
        return lines.stream()
            .filter(line -> (line.isSameName(name) || line.isSameColor(color)) && line
                .isNotSameId(lineId))
            .findAny();
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
