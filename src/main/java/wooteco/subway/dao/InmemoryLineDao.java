package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class InmemoryLineDao implements LineDao {

    private static InmemoryLineDao INSTANCE;

    private Long seq = 0L;
    private final Map<Long, Line> lines = new HashMap<>();

    private InmemoryLineDao() {
    }

    public static synchronized InmemoryLineDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InmemoryLineDao();
        }
        return INSTANCE;
    }

    public void clear() {
        lines.clear();
    }

    @Override
    public Line save(final Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Line findById(final Long id) {
        return lines.get(id);
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public boolean existByName(final String name) {
        return lines.values()
                .stream()
                .anyMatch(line -> line.isSameName(name));
    }

    @Override
    public boolean existById(Long id) {
        return lines.containsKey(id);
    }

    @Override
    public void update(final Line line) {
        lines.put(line.getId(), line);
    }

    @Override
    public void delete(final Long id) {
        lines.remove(id);
    }
}
