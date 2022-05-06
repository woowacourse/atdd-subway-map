package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    @Override
    public Optional<Line> save(final Line line) {
        if (isDuplicateName(line)) {
            return Optional.empty();
        }

        final Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return Optional.of(persistLine);
    }

    private boolean isDuplicateName(final Line line) {
        return lines.stream()
                .anyMatch(it -> it.isSameName(line));
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    private Line createNewObject(final Line line) {
        final Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Optional<Line> findById(final Long id) {
        return lines.stream()
                .filter(it -> it.isSameId(id))
                .findFirst();
    }

    @Override
    public Line updateById(final Long id, final Line line) {
        final Line persistLine = findById(id).orElseThrow();
        persistLine.update(line);
        return persistLine;
    }

    @Override
    public Integer deleteById(final Long id) {
        final Line line = findById(id).orElseThrow();
        final boolean isDeleted = lines.remove(line);
        if (isDeleted) {
            return 1;
        }
        return 0;
    }
}
