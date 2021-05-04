package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;

public class LineDaoCache implements LineDao {

    private Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

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

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
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
}
