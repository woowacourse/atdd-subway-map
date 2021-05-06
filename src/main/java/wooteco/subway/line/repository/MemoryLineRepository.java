package wooteco.subway.line.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.line.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryLineRepository implements LineRepository {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        validateDuplicateName(line.getName());
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
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findFirst()
                .get();
    }

    @Override
    public void delete(Long id) {
        Line findByIdLine = findById(id);
        lines.remove(findByIdLine);
    }

    @Override
    public void update(Line line) {
        validateDuplicateName(line.getName());
        delete(line.getId());
        lines.add(line);
    }

    @Override
    public void deleteAll() {
        lines.clear();
    }

    private void validateDuplicateName(String name) {
        if (lines.stream()
                .anyMatch(line -> line.isSameName(name))) {
            throw new DuplicatedNameException();
        }
    }
}
