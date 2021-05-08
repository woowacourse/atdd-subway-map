package wooteco.subway.line.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InMemoryLineDao implements LineRepository {
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
    public boolean validateDuplicateName(String name) {
        return lines.stream()
                .anyMatch(line -> line.isSameName(name));
    }

    @Override
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public boolean validateUsableName(String oldName, String newName) {
        return lines.stream()
                .filter(line -> !line.isSameName(oldName))
                .anyMatch(line -> line.isSameName(newName));
    }

    @Override
    public void updateById(Long id, Line updatedLine) {
        Line line = findByIdIfExist(id);
        lines.stream()
                .filter(line::equals)
                .findAny()
                .ifPresent(l -> l.update(updatedLine));
    }

    private Line findByIdIfExist(Long id) {
        return findById(id);
    }

    @Override
    public void delete(Long id) {
        Line line = findByIdIfExist(id);
        lines.remove(line);
    }
}
