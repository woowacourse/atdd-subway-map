package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.ExceptionMessage;

public class FakeLineDao implements LineDao {

    private final List<Line> lines = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Line save(final Line line) {
        if (isDuplicateName(line)) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_LINE_NAME.getContent());
        }

        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
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
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(it -> it.isSameId(id))
                .findFirst();
    }

    @Override
    public Optional<Line> update(final Line line) {
        Line foundLine = findById(line.getId()).get();
        foundLine.update(line);
        return Optional.of(foundLine);
    }

    @Override
    public Integer deleteById(Long id) {
        Line line = findById(id).get();
        boolean isDeleted = lines.remove(line);
        if (isDeleted) {
            return 1;
        }
        return 0;
    }
}
