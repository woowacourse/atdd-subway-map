package wooteco.subway.line.repository.infra;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryLineRepository implements LineRepository {

    private static Long seq = 0L;
    private static final List<Line> LINES = new ArrayList<>();

    public Line save(final Line line) {
        validateDuplicateName(line.getName());
        Line persistLine = createNewObject(line);
        LINES.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(final Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return Collections.unmodifiableList(LINES);
    }

    @Override
    public Line findById(final Long id) {
        return LINES.stream()
                .filter(line -> line.isSameId(id))
                .findFirst()
                .get();
    }

    @Override
    public void delete(final Long id) {
        Line findByIdLine = findById(id);
        LINES.remove(findByIdLine);
    }

    @Override
    public void update(final Line line) {
        validateDuplicateName(line.getName());
        delete(line.getId());
        LINES.add(line);
    }

    @Override
    public void deleteAll() {
        LINES.clear();
    }

    private void validateDuplicateName(final String name) {
        if (LINES.stream()
                .anyMatch(line -> line.isSameName(name))) {
            throw new DuplicatedNameException();
        }
    }
}
