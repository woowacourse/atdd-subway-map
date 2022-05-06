package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
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

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public boolean existLineById(Long id) {
        List<Long> lineNames = lines.stream()
                .map(Line::getId)
                .collect(Collectors.toList());
        return lineNames.contains(id);
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
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmptyResultDataAccessException("존재하지 않는 노선입니다.", 1));
    }

    @Override
    public void update(Line line) {
        delete(line.getId());
        lines.add(line);
    }

    @Override
    public void delete(Long id) {
        lines.removeIf(line -> line.getId().equals(id));
    }

    public void clear() {
        lines.clear();
    }
}
