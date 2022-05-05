package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineMockDao implements LineDao {

    private static final int TRUE = 1;
    private static final int FALSE = 0;

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    @Override
    public long save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine.getId();
    }

    @Override
    public boolean existLineByName(String name) {
        List<String> lineNames = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        return lineNames.contains(name);
    }

    @Override
    public boolean existLineByColor(String color) {
        List<String> lineColors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());
        return lineColors.contains(color);
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Optional<Line> find(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst();
    }

    @Override
    public int update(long id, Line line) {
        if (delete(id) == TRUE) {
            lines.add(line);
            return TRUE;
        }
        return FALSE;
    }

    @Override
    public int delete(Long id) {
        boolean isRemoving = lines.removeIf(line -> line.getId().equals(id));
        if (!isRemoving) {
            return FALSE;
        }
        return TRUE;
    }

    public void clear() {
        lines.clear();
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
