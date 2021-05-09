package wooteco.subway.line.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.line.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MemoryLineDao implements LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        validateDuplicateName(line);
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
        return Collections.unmodifiableList(lines);
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.equalId(id))
                .findAny();
    }

    @Override
    public void delete(Long id) {
        lines.removeIf(line -> line.equalId(id));
    }

    @Override
    public void update(Line line, Long id) {
        validateDuplicateName(line);
        delete(id);
        lines.add(line);
    }

    @Override
    public void deleteAll() {
        lines.clear();
    }

    @Override
    public Optional<Line> findByName(String name) {
        return lines.stream()
                .filter(line -> line.equalName(name))
                .findAny();
    }

    private void validateDuplicateName(Line newLine) {
        if (lines.stream()
                .anyMatch(line -> line.equalName(newLine.getName()))) {
            throw new DuplicatedNameException();
        }
    }
}
