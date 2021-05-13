package wooteco.subway.line.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.line.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InMemoryLineDao implements LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

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
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny()
                .orElseThrow(StationNotFoundException::new);
    }

    @Override
    public void update(Line updatedLine) {
        Line line = findById(updatedLine.getId());
        int index = lines.indexOf(line);
        lines.set(index, updatedLine);
    }

    @Override
    public void delete(Long id) {
        Line line = findById(id);
        lines.remove(line);
    }

    @Override
    public boolean existByName(String name) {
        return lines.stream()
                .anyMatch(line -> line.isSameName(name));
    }

    @Override
    public boolean existByNameAndNotInOriginalName(String name, String originalName) {
        return lines.stream()
                .filter(line -> !line.isSameName(originalName))
                .anyMatch(line -> line.isSameName(name));
    }
}
