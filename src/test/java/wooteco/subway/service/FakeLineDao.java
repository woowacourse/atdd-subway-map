package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.ExceptionMessage;

public class FakeLineDao implements LineDao {

    private Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

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
    public Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.isSameId(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NOT_FOUND_LINE_BY_ID.getContent()));
    }

    @Override
    public Line updateById(final Long id, final Line line) {
        final Line persistLine = findById(id);
        persistLine.update(line);
        return persistLine;
    }

    @Override
    public Integer deleteById(Long id) {
        Line line = findById(id);
        boolean isDeleted = lines.remove(line);
        if (isDeleted) {
            return 1;
        }
        return 0;
    }
}
